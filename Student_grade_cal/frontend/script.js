// ============================================
// GRADE CALCULATOR - FRONTEND
// ============================================

const API_URL = "http://localhost:8080";

// HTML ELEMENTS
const calculateButton = document.getElementById("calculateButton");
const resetButton = document.getElementById("resetButton");
const message = document.getElementById("message");

const total = document.getElementById("total");
const percentage = document.getElementById("percentage");
const grade = document.getElementById("grade");

// ============================================
// CALCULATE RESULT
// ============================================

async function calculateResult() {

    const marks = [];

    // Read all five subject marks
    for (let i = 1; i <= 5; i++) {

        const input = document.getElementById("subject" + i);

        if (!input) {
            console.error("Missing HTML element: subject" + i);
            return;
        }

        const value = input.value.trim();

        if (value === "") {
            message.textContent = "⚠️ Please enter marks for all subjects.";
            message.style.color = "#dc2626";
            return;
        }

        const mark = Number(value);

        if (!Number.isFinite(mark) || mark < 0 || mark > 100) {
            message.textContent = "⚠️ Marks must be between 0 and 100.";
            message.style.color = "#dc2626";
            return;
        }

        marks.push(mark);
    }

    try {

        calculateButton.disabled = true;
        calculateButton.textContent = "CALCULATING...";

        // Prepare query parameters
        const params = new URLSearchParams();

        marks.forEach((mark, index) => {
            params.append("mark" + (index + 1), mark);
        });

        // Call Java backend
        const response = await fetch(
            `${API_URL}/calculate?${params.toString()}`
        );

        const data = await response.json();

        console.log("Backend response:", data);

        if (!response.ok) {
            throw new Error(data.message || "Backend calculation failed.");
        }

        // Validate response
        if (
            data.total === undefined ||
            data.percentage === undefined ||
            data.grade === undefined
        ) {
            throw new Error(
                "Unexpected backend response: " + JSON.stringify(data)
            );
        }

        // Display result
        total.textContent = `${data.total} / 500`;
        percentage.textContent = `${data.percentage}%`;
        grade.textContent = data.grade;

        message.textContent = "✅ Result calculated successfully!";
        message.style.color = "#16a34a";

    } catch (error) {

        console.error("Calculation error:", error);

        message.textContent = "⚠️ " + error.message;
        message.style.color = "#dc2626";

    } finally {

        calculateButton.disabled = false;
        calculateButton.textContent = "CALCULATE RESULT";
    }
}

// ============================================
// RESET
// ============================================

function resetResult() {

    for (let i = 1; i <= 5; i++) {

        const input = document.getElementById("subject" + i);

        if (input) {
            input.value = "";
        }
    }

    total.textContent = "0 / 500";
    percentage.textContent = "0%";
    grade.textContent = "-";

    message.textContent = "";
}

// ============================================
// EVENT LISTENERS
// ============================================

calculateButton.addEventListener("click", calculateResult);
resetButton.addEventListener("click", resetResult);