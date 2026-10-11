const API_URL = "http://localhost:8080";


// ==============================
// QUIZ DATA
// ==============================

let questions = [];

let currentQuestion = 0;

let score = 0;

let selectedAnswer = -1;

let timer;

let timeLeft = 10;

let answers = [];


// ==============================
// START QUIZ
// ==============================

async function startQuiz() {

    try {

        const response =
            await fetch(`${API_URL}/questions`);

        if (!response.ok) {

            throw new Error("Backend error");

        }

        questions = await response.json();

        currentQuestion = 0;

        score = 0;

        answers = [];

        document
            .getElementById("startScreen")
            .classList.add("hidden");

        document
            .getElementById("resultScreen")
            .classList.add("hidden");

        document
            .getElementById("quizScreen")
            .classList.remove("hidden");

        showQuestion();

    }

    catch (error) {

        alert(
            "Unable to connect to Java backend.\n\n" +
            "Please make sure QuizServer is running."
        );

        console.error(error);

    }

}


// ==============================
// SHOW QUESTION
// ==============================

function showQuestion() {

    clearInterval(timer);

    selectedAnswer = -1;

    timeLeft = 10;

    const questionData =
        questions[currentQuestion];


    document.getElementById("questionNumber")
        .textContent =
        `Question ${currentQuestion + 1} of ${questions.length}`;


    document.getElementById("question")
        .textContent =
        questionData.question;


    document.getElementById("timer")
        .textContent =
        timeLeft;


    document.getElementById("message")
        .textContent = "";


    // Progress

    const progress =
        ((currentQuestion + 1) /
            questions.length) * 100;

    document.getElementById("progressBar")
        .style.width =
        progress + "%";


    // Options

    const optionsDiv =
        document.getElementById("options");

    optionsDiv.innerHTML = "";


    questionData.options.forEach(
        (option, index) => {

            const div =
                document.createElement("div");

            div.className = "option";

            div.innerHTML = `

                <label>

                    <input
                        type="radio"
                        name="answer"
                        value="${index}"
                    >

                    ${option}

                </label>

            `;


            div.onclick = function () {

                selectOption(index);

            };


            optionsDiv.appendChild(div);

        }
    );


    startTimer();

}


// ==============================
// SELECT OPTION
// ==============================

function selectOption(index) {

    selectedAnswer = index;


    const allOptions =
        document.querySelectorAll(".option");


    allOptions.forEach(
        option => {

            option.classList.remove("selected");

        }
    );


    allOptions[index]
        .classList.add("selected");


    const radio =
        allOptions[index]
            .querySelector("input");

    radio.checked = true;

}


// ==============================
// TIMER
// ==============================

function startTimer() {

    timer =
        setInterval(() => {

            timeLeft--;

            document.getElementById("timer")
                .textContent =
                timeLeft;


            if (timeLeft <= 0) {

                clearInterval(timer);

                submitAnswer(true);

            }

        }, 1000);

}


// ==============================
// SUBMIT ANSWER
// ==============================

async function submitAnswer(timeUp = false) {

    clearInterval(timer);


    if (selectedAnswer === -1) {

        if (!timeUp) {

            document.getElementById("message")
                .textContent =
                "Please select an answer.";

            document.getElementById("message")
                .style.color =
                "#dc2626";

            return;

        }

    }


    const questionData =
        questions[currentQuestion];


    let isCorrect = false;


    try {

        const response =
            await fetch(
                `${API_URL}/check?question=${currentQuestion}&answer=${selectedAnswer}`
            );


        const result =
            await response.json();


        isCorrect = result.correct;


        if (isCorrect) {

            score++;

        }


        answers.push({

            question:
                questionData.question,

            selected:
                selectedAnswer === -1
                    ? "No Answer"
                    : questionData.options[selectedAnswer],

            correct:
                questionData.options[result.correctAnswer],

            isCorrect:
                isCorrect

        });


    }

    catch (error) {

        console.error(error);

        return;

    }


    // Next question

    currentQuestion++;


    if (currentQuestion < questions.length) {

        showQuestion();

    }

    else {

        showResult();

    }

}


// ==============================
// SHOW RESULT
// ==============================

function showResult() {

    document
        .getElementById("quizScreen")
        .classList.add("hidden");


    document
        .getElementById("resultScreen")
        .classList.remove("hidden");


    document.getElementById("finalScore")
        .textContent =
        `${score} / ${questions.length}`;


    const percentage =
        (score / questions.length) * 100;


    document.getElementById("percentage")
        .textContent =
        `${percentage}%`;


    const summary =
        document.getElementById("summary");


    summary.innerHTML = "";


    answers.forEach(
        (answer, index) => {

            const div =
                document.createElement("div");


            div.className =
                "summary-item " +
                (answer.isCorrect
                    ? "correct"
                    : "incorrect");


            div.innerHTML = `

                <h4>
                    ${index + 1}.
                    ${answer.question}
                </h4>

                <p>
                    <b>Your Answer:</b>
                    ${answer.selected}
                </p>

                <p>
                    <b>Correct Answer:</b>
                    ${answer.correct}
                </p>

                <p>
                    <b>Result:</b>
                    ${answer.isCorrect
                        ? "Correct ✓"
                        : "Incorrect ✗"}
                </p>

            `;


            summary.appendChild(div);

        }
    );

}


// ==============================
// RESTART QUIZ
// ==============================

function restartQuiz() {

    clearInterval(timer);


    document
        .getElementById("resultScreen")
        .classList.add("hidden");


    document
        .getElementById("startScreen")
        .classList.remove("hidden");


    document.getElementById("summary")
        .innerHTML = "";

}