// ============================================
// ATM INTERFACE - FRONTEND
// ============================================

const API_URL = "http://localhost:8080";


// ============================================
// HTML ELEMENTS
// ============================================

const balanceElement =
    document.getElementById("balance");

const messageElement =
    document.getElementById("message");

const transactionElement =
    document.getElementById("transaction");


// ============================================
// SHOW OPERATION
// ============================================

function showSection(sectionId) {

    document
        .getElementById("withdraw")
        .classList.add("hidden");

    document
        .getElementById("deposit")
        .classList.add("hidden");


    document
        .getElementById(sectionId)
        .classList.remove("hidden");


    messageElement.textContent =
        "Enter the amount and continue.";

    messageElement.style.color =
        "#475569";
}


// ============================================
// WITHDRAW
// ============================================

async function withdraw() {

    const input =
        document.getElementById("withdrawAmount");

    const amount =
        Number(input.value);


    if (!input.value || amount <= 0) {

        showMessage(
            "⚠️ Please enter a valid amount.",
            "#dc2626"
        );

        return;
    }


    try {

        const response =
            await fetch(
                `${API_URL}/withdraw?amount=${amount}`
            );


        const data =
            await response.json();


        updateBalance(data.balance);


        if (data.success) {

            showMessage(
                "✅ " + data.message,
                "#16a34a"
            );

            transactionElement.textContent =
                "Withdrawn: ₹" +
                amount.toFixed(2);

            input.value = "";

        } else {

            showMessage(
                "❌ " + data.message,
                "#dc2626"
            );
        }


    } catch (error) {

        console.error(error);

        showMessage(
            "⚠️ Unable to connect to Java backend.",
            "#dc2626"
        );
    }
}


// ============================================
// DEPOSIT
// ============================================

async function deposit() {

    const input =
        document.getElementById("depositAmount");

    const amount =
        Number(input.value);


    if (!input.value || amount <= 0) {

        showMessage(
            "⚠️ Please enter a valid amount.",
            "#dc2626"
        );

        return;
    }


    try {

        const response =
            await fetch(
                `${API_URL}/deposit?amount=${amount}`
            );


        const data =
            await response.json();


        updateBalance(data.balance);


        if (data.success) {

            showMessage(
                "✅ " + data.message,
                "#16a34a"
            );

            transactionElement.textContent =
                "Deposited: ₹" +
                amount.toFixed(2);

            input.value = "";

        } else {

            showMessage(
                "❌ " + data.message,
                "#dc2626"
            );
        }


    } catch (error) {

        console.error(error);

        showMessage(
            "⚠️ Unable to connect to Java backend.",
            "#dc2626"
        );
    }
}


// ============================================
// CHECK BALANCE
// ============================================

async function checkBalance() {

    try {

        const response =
            await fetch(
                `${API_URL}/balance`
            );


        const data =
            await response.json();


        updateBalance(data.balance);


        showMessage(
            "💳 Current balance checked successfully.",
            "#2563eb"
        );


        transactionElement.textContent =
            "Balance checked: ₹" +
            data.balance.toFixed(2);


    } catch (error) {

        console.error(error);

        showMessage(
            "⚠️ Unable to connect to Java backend.",
            "#dc2626"
        );
    }
}


// ============================================
// RESET
// ============================================

async function resetATM() {

    try {

        const response =
            await fetch(
                `${API_URL}/reset`
            );


        const data =
            await response.json();


        updateBalance(data.balance);


        showMessage(
            "🔄 Account reset successfully.",
            "#2563eb"
        );


        transactionElement.textContent =
            "Account reset. Balance: ₹" +
            data.balance.toFixed(2);


        document
            .getElementById("withdrawAmount")
            .value = "";

        document
            .getElementById("depositAmount")
            .value = "";


    } catch (error) {

        console.error(error);

        showMessage(
            "⚠️ Unable to connect to Java backend.",
            "#dc2626"
        );
    }
}


// ============================================
// UPDATE BALANCE
// ============================================

function updateBalance(balance) {

    balanceElement.textContent =
        "₹" + balance.toFixed(2);
}


// ============================================
// SHOW MESSAGE
// ============================================

function showMessage(
    text,
    color
) {

    messageElement.textContent =
        text;

    messageElement.style.color =
        color;
}