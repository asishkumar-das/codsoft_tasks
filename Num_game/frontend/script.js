
// ============================================
// NUMBER GUESSING GAME - FRONTEND
// ============================================

// Backend URL
const API_URL = "http://localhost:8080";


// ============================================
// GAME STATE
// ============================================

let round = 1;
let attempts = 0;
let score = 0;

const MAX_ATTEMPTS = 7;


// ============================================
// HTML ELEMENTS
// ============================================

const guessInput = document.getElementById("guessInput");
const guessButton = document.getElementById("guessButton");

const newRoundButton = document.getElementById("newRoundButton");
const resetButton = document.getElementById("resetButton");

const message = document.getElementById("message");
const messageIcon = document.getElementById("messageIcon");

const roundDisplay = document.getElementById("round");
const attemptsDisplay = document.getElementById("attempts");
const scoreDisplay = document.getElementById("score");

const progressText = document.getElementById("progressText");
const progressFill = document.getElementById("progressFill");


// ============================================
// UPDATE UI
// ============================================

function updateUI() {

    roundDisplay.textContent = round;

    attemptsDisplay.textContent =
        `${attempts} / ${MAX_ATTEMPTS}`;

    scoreDisplay.textContent = score;

    progressText.textContent =
        `${attempts} / ${MAX_ATTEMPTS}`;

    const percentage =
        (attempts / MAX_ATTEMPTS) * 100;

    progressFill.style.width =
        `${percentage}%`;
}


// ============================================
// SHOW MESSAGE
// ============================================

function showMessage(text, icon = "💡") {

    message.textContent = text;

    messageIcon.textContent = icon;
}


// ============================================
// CHECK GUESS
// ============================================

async function checkGuess() {

    const guess = Number(guessInput.value);


    // Validation
    if (!guessInput.value) {

        showMessage(
            "Please enter a number!",
            "⚠️"
        );

        return;
    }


    if (guess < 1 || guess > 100) {

        showMessage(
            "Enter a number between 1 and 100!",
            "⚠️"
        );

        return;
    }


    try {

        guessButton.disabled = true;


        // Send guess to Java backend
        const response = await fetch(
            `${API_URL}/guess?number=${guess}`
        );


        if (!response.ok) {

            throw new Error(
                "Server response error"
            );
        }


        const data = await response.json();


        // Update game data
        attempts = data.attempts;

        score = data.score;


        // Correct
        if (data.correct) {

            showMessage(
                data.message,
                "🎉"
            );

            guessButton.disabled = true;

        }

        // Too low
        else if (data.result === "LOW") {

            showMessage(
                data.message,
                "📉"
            );

            guessButton.disabled = false;
        }

        // Too high
        else if (data.result === "HIGH") {

            showMessage(
                data.message,
                "📈"
            );

            guessButton.disabled = false;
        }

        // Game over
        if (data.gameOver) {

            showMessage(
                data.message,
                "❌"
            );

            guessButton.disabled = true;
        }


        guessInput.value = "";

        updateUI();

        guessInput.focus();

    }

    catch (error) {

        console.error(error);

        showMessage(
            "Unable to connect to Java backend.",
            "⚠️"
        );

        guessButton.disabled = false;
    }
}


// ============================================
// START NEW ROUND
// ============================================

async function startNewRound() {

    try {

        await fetch(
            `${API_URL}/new-round`
        );

        round++;

        attempts = 0;

        guessButton.disabled = false;

        guessInput.disabled = false;

        guessInput.value = "";

        showMessage(
            "New round started! Make your guess.",
            "🎯"
        );

        updateUI();

        guessInput.focus();

    }

    catch (error) {

        showMessage(
            "Unable to start new round.",
            "⚠️"
        );
    }
}


// ============================================
// RESET GAME
// ============================================

async function resetGame() {

    try {

        await fetch(
            `${API_URL}/reset`
        );

        round = 1;

        attempts = 0;

        score = 0;

        guessButton.disabled = false;

        guessInput.disabled = false;

        guessInput.value = "";

        showMessage(
            "Game has been reset!",
            "🔄"
        );

        updateUI();

        guessInput.focus();

    }

    catch (error) {

        showMessage(
            "Unable to reset game.",
            "⚠️"
        );
    }
}


// ============================================
// EVENT LISTENERS
// ============================================

guessButton.addEventListener(
    "click",
    checkGuess
);


newRoundButton.addEventListener(
    "click",
    startNewRound
);


resetButton.addEventListener(
    "click",
    resetGame
);


// Press ENTER to guess

guessInput.addEventListener(
    "keydown",
    function (event) {

        if (event.key === "Enter") {

            checkGuess();
        }
    }
);


// ============================================
// INITIAL UI
// ============================================

updateUI();

