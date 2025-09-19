var tempLanguage = '';
var tempAmount = '';
var tempScreenID = '';
var tempPopupMainText = '';

var excludedScreens = [""];
var balanceHeadlines = ["Шоттағы қалдық", "Остаток на счете", "Balance Inquiry"];
var totalHeadlines = ["Қолма-қол ақша енгізу", "Взнос наличных", "Deposit Notes"];
var withdrawalHeadlines = ["Қолма-қол ақша алу", "Выдача наличности", "Withdraw Notes"];
var pinEntryHeadlines = ["Сіз қате PIN енгіздіңіз!", "Вы ввели неверный PIN!", "PIN code entered is incorrect!"];

var language = '';
var amountFirstPlayed = false;
var screenPlayedAt = 0;
var generalInputPlayedAt = 0;
var popupPlayedAt = 0;
var receiptPlayedAt = 0;
var pinEntryPlayedAt = 0;

/* ********************************************************* */

// Устанавливаем периодическую проверку
setInterval(async function () {
	// Получаем iframe до подключения наушников, так как при следующей итерации размытие должно сброситься
	let iframe = document.getElementById('applicationContent');
	if (!iframe) return console.error("iframe not found");
	// Получаем элементы внутри iframe
	let iframeDoc = iframe.contentDocument;
	if (!iframeDoc) return console.error("iframeDoc not found");

    // Получаем состояние headset
	let headset = await Wincor.UI.Service.Provider.DataService.getValues("DCWEB_HEADSET_PLUGGED");
	// Если headset подключен
	if (headset.DCWEB_HEADSET_PLUGGED === '1') {
        // Добавляем размытие
		activateBlur(iframeDoc);
        // Активируем окно выбора языка
        Wincor.UI.Service.Provider.DataService.setValues("BSTAFW_PROP_HEADPHONES_STATUS", 1);
        // Wincor.UI.Service.Provider.ViewService.endView(Wincor.UI.Service.Provider.ViewService.UIRESULT_OK, "WITHDRAWAL", "WITHDRAWAL");

		// Получаем значение элемента lang
		let lang = Wincor.UI.Service.Provider.LocalizeService.currentLanguage;
		if (!lang) return console.error("language param not found");

		// При смене языка, параметры сбрасываются
		switch (lang) {
			case 'en-US':
				language = 'en';
				updateLanguage(language);
				break;
			case 'ru-RU':
				language = 'ru';
				updateLanguage(language);
				break;
			case 'kk-KZ':
				language = 'kz';
				updateLanguage(language);
				break;
			default:
				return console.error("language not found");
		}

		// Получаем id экрана
		let bodyElement = iframeDoc.querySelector('body');
		if (!bodyElement) return console.error("bodyElement not found");
		let screenID = bodyElement.getAttribute('data-view-key');
		if (!screenID) return console.error("screenID not found");

		console.log(screenID);

		// Воспроизводим все, кроме исключенных
		if (!excludedScreens.includes(screenID)) {

		    // Воспроизводим, если screenID поменялся
		    if (screenID !== tempScreenID && screenID !== "ReceiptInfo" && screenID !== "PinEntry") {
				if (Date.now() - screenPlayedAt > 2_000) { // Прошло больше 2 секунд
					tempScreenID = screenID; // Обновляем tempScreenID
                	amountFirstPlayed = false;

					await playbackScreen(screenID, language); // Воспроизводим аудио файл(ы)

					screenPlayedAt = Date.now();
				}
            }

            // Получаем значение элемента generalInput для озвучки вводимой с клавиатуры суммы
            let inputField = iframeDoc.getElementById('generalInput');
            if (inputField) {
                // Очищаем поле для ввода суммы от двух первых символов '₸ '
                let amount = cleanAmount(inputField.value);
                // Воспроизводим, если amount поменялся
                if (amount !== tempAmount) {
					if (Date.now() - generalInputPlayedAt > 2_000) { // Прошло больше 2 секунд
						tempAmount = amount; // Обновляем tempAmount

						if (amount !== '0') {
                       		amountFirstPlayed = true;

							await playbackAmount(amount, language); // Воспроизводим сумму при каждом изменении
						} else if (amountFirstPlayed === true) {
							amountFirstPlayed = false;

							await playbackButton('buttonCorrect', language); // Воспроизводим аудио файл(ы) для нажатой кнопки
						}

						generalInputPlayedAt = Date.now();
					}
                }
            }

            // Получаем значение элемента popupMain для озвучки всплывающего окна
            let popupMain = iframeDoc.getElementById('popupMain');
            if (popupMain) {
                let popupMainText = popupMain.innerText;
                if (popupMainText !== tempPopupMainText) {
					if (Date.now() - popupPlayedAt > 2_000) { // Прошло больше 2 секунд
                    	tempPopupMainText = popupMainText; // Обновляем tempPopupMain

					    await playbackScreen("PopupMain", language); // Воспроизводим аудио файл(ы)

						popupPlayedAt = Date.now();
					}
                }
            }

            if (screenID !== tempScreenID && screenID === "ReceiptInfo") {
				if (Date.now() - receiptPlayedAt > 2_000) { // Прошло больше 2 секунд
					tempScreenID = screenID; // Обновляем tempScreenID

					// Получаем значение элемента MESSAGE_HEADER___generated для озвучки суммы транзакции
					let varBlockSum = iframeDoc.getElementById("MESSAGE_HEADER___generated");
					if (varBlockSum) {
						// Берем весь текст
						let varBlockSumText = varBlockSum.innerText;
						// Ищем число перед "KZT"
						let matchSum = varBlockSumText.match(/\s*(-?\d+)(?:\.(\d{1,2}))?\s*KZT/);

						if (matchSum) {
							// число со знаком, например "-123" или "456"
							let numberStr = matchSum[1];
							// знак
							let sign = numberStr.startsWith("-") ? "-" : "";
							// целая часть (без знака)
							let integerPart = Math.abs(parseInt(numberStr, 10));
							// дробная часть (строка с 2 цифрами)
							let fractionalPart = matchSum[2] ? matchSum[2].padEnd(2, "0") : "00";
							// Получаем значение headline
							let headline = iframeDoc.getElementById("headline").textContent;

							if (balanceHeadlines.includes(headline)) {
								await playbackBalance(sign, integerPart, fractionalPart, language);
							} else if (totalHeadlines.includes(headline)) {
								await playbackTotal(integerPart, language);
							} else if (withdrawalHeadlines.includes(headline)) {
								await playbackScreen(screenID, language);
							}
						}
					}

					receiptPlayedAt = Date.now();
				}
            }

			if (screenID !== tempScreenID && screenID === "PinEntry") {
				let headline = iframeDoc.getElementById("headline").innerText.split("\n")[0].trim();
				console.log(headline);

				if (Date.now() - pinEntryPlayedAt > 2_000) { // Прошло больше 2 секунд
					tempScreenID = screenID; // Обновляем tempScreenID

					if (pinEntryHeadlines.includes(headline)) {
						await playbackScreen("PinEntryWrong", language);
					} else {
						await playbackScreen(screenID, language); // Воспроизводим аудио файл(ы)
					}

					pinEntryPlayedAt = Date.now();
				}
			}
		}
	} else {
		// Убираем размытие
		deactivateBlur(iframeDoc);
		// Деактивируем окно выбора языка
		Wincor.UI.Service.Provider.DataService.setValues("BSTAFW_PROP_HEADPHONES_STATUS", 0);
		// Останавливаем предыдущее воспроизведение, если есть
		await stopPlayback();

		// Сбрасываем все параметры
		tempLanguage = '';
		tempAmount = '';
		tempScreenID = '';
		tempPopupMainText = '';
	}
}, 1000); // Проверяем каждые 1000 мс

/* ********************************************************* */

async function playbackScreen(screenID, language) {
	// Получаем список файлов для воспроизведения, соответствующих текущему экрану
	let data = await getScreenMapping(screenID, language);
	if (data?.mapping) {
		await playback(data.mapping, language); // Воспроизводим файлы
	} else {
		console.log('No associated audio files for screen');
		// Останавливаем предыдущее воспроизведение на новом экране, даже если сопоставленные данные не найдены
		await stopPlayback();
	}
}

async function playbackButton(buttonId, language) {
	// Получаем аудиофайлы, сопоставленные кнопке
	let data = await getButtonMapping(buttonId, language);
	if (data?.mapping) {
		await playback(data.mapping, language); // Воспроизводим файлы
	} else {
		console.log('No associated audio files for button');
	}
}

async function playbackAmount(amount, language) {
	// Получаем аудиофайлы, сопоставленные для воспроизведения суммы снятия
	let response = await getAmountMapping('amount.txt', language);
	let audioFiles = response.mapping.map(item => {
		if (item === "$amount") {
			return String(amount);
		} else {
			return item; // Оставляем элемент без изменений
		}
	});
	await playback(audioFiles, language); // Воспроизводим сумму снятия и сопроводительные файлы
}

async function playbackTotal(total, language) {
	// Получаем аудиофайлы, сопоставленные для воспроизведения суммы пополнения
	let response = await getTotalMapping('total.txt', language);
	let audioFiles = response.mapping.map(item => {
		if (item === "$total") {
			return String(total);
		} else {
			return item; // Оставляем элемент без изменений
		}
	});
	await playback(audioFiles, language); // Воспроизводим сумму пополнения и сопроводительные файлы
}

async function playbackBalance(sign, integerPart, fractionalPart, language) {
	// Получаем аудиофайлы, сопоставленные для воспроизведения суммы баланса
	let response = await getBalanceMapping('balance.txt', language);
	let audioFiles = response.mapping.map(item => {
		if (item === "$sign") {
			if (sign === '-') {
				return String('minus.wav');
			} else {
                return String('delay_01.wav');
            }
		} else if (item === "$integer") {
			return String(integerPart);
		} else if (item === "$fractional") {
			return String(fractionalPart);
		} else {
			return item; // Оставляем элемент без изменений
		}
	});
	await playback(audioFiles, language); // Воспроизводим сумму баланса и сопроводительные файлы
}

/* ********************************************************* */

function cleanAmount(amount) {
    return amount.replace(/₸\s?/g, '').replace(/\./g, '');
}

function updateLanguage(language) {
	if (language !== tempLanguage) {
		tempLanguage = language;
		tempAmount = '';
		tempScreenID = '';
		tempPopupMainText = '';
	}
}

function activateBlur(iframeDoc) {
  iframeDoc.body.style.filter = "blur(2px)";
  //iframeDoc.body.style.pointerEvents = "none"; // блокируем клики
}

function deactivateBlur(iframeDoc) {
  iframeDoc.body.style.filter = "";
  //iframeDoc.body.style.pointerEvents = "";
}

/* ********************************************************* */

function playback(mapping, language) {
	return sendPostRequest('http://localhost:8081/audio/playback', { audioFiles: mapping, language });
}

function stopPlayback() {
	return sendGetRequest('http://localhost:8081/audio/stop-playback');
}

function getScreenMapping(screenID, language) {
	return sendPostRequest('http://localhost:8081/mapping/screen', { screenID, language });
}

function getButtonMapping(buttonID, language) {
	return sendPostRequest('http://localhost:8081/mapping/button', { buttonID, language });
}

function getAmountMapping(filename, language) {
	return sendPostRequest('http://localhost:8081/mapping/amount', { filename, language });
}

function getTotalMapping(filename, language) {
	return sendPostRequest('http://localhost:8081/mapping/total', { filename, language });
}

function getBalanceMapping(filename, language) {
	return sendPostRequest('http://localhost:8081/mapping/balance', { filename, language });
}

/* ********************************************************* */

async function sendPostRequest(url, data) {
	try {
		let response = await fetch(url, {
			method: 'POST',
			headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' },
			body: JSON.stringify(data)
		});
		if (!response.ok) throw new Error('Network response was not ok');
		let result = await response.json();
		console.log('POST response:', result, data);
		return result;
	} catch (error) {
		console.error('Error:', error);
	}
}

async function sendGetRequest(url) {
	try {
		let response = await fetch(url, {
			method: 'GET',
			headers: { 'Accept': 'text/plain' }
		});
		if (!response.ok) throw new Error('Network response was not ok');
		let result = await response.text();
		console.log('GET response:', result, url);
		return result;
	} catch (error) {
		console.error('Error:', error);
	}
}

/* ********************************************************* */

/*

--- <<< Код ListViewModel.js >>> ---
try {
    // Очистка перед циклом
    localStorage.setItem("myElements", "[]");

    for(let i = 0; i < srcLen; i++) {
        let item = elements[i];

        // Проверка условия
        if (typeof item.prominent === "string") {
            const match = item.prominent.match(/LOV(\d)/); // ищем LOV + цифру
            if (match) {
                item.eppkey = parseInt(match[1], 10); // присваиваем eppkey цифру после LOV
                item.currentViewId = localStorage.getItem("currentViewId"); // присваиваем currentViewId
            }
        }

        // Запись элементов в localStorage
        try {
            // Клонируем объект (без функций)
            const safeItem = JSON.parse(JSON.stringify(item));
            // Считываем уже сохранённые элементы (или создаём новый массив)
            let stored = JSON.parse(localStorage.getItem("myElements")) || [];
            // Добавляем текущий элемент
            stored.push({ index: i, element: safeItem });
            // Сохраняем обратно
            localStorage.setItem("myElements", JSON.stringify(stored));
        } catch (err) {
            // Если ошибка — тоже достаём массив
            let stored = JSON.parse(localStorage.getItem("myElements")) || [];
            stored.push({ index: i, error: `[Error serializing item: ${err}]` });
            localStorage.setItem("myElements", JSON.stringify(stored));
        }

--- <<< Код start.html >>> ---
<script src="../../lib/nts.js"></script>

--- <<< Код selection.html >>> ---
command: {id: $data.result, type: $root.CMDTYPE.BUTTON, state: $data.state, eppkey: $data.eppkey }">

--- <<< Код selectioncancel.html >>> ---
command: {id: $data.result, type: $root.CMDTYPE.BUTTON, state: $data.state, eppkey: $data.eppkey }">

--- <<< Код amountselection.html >>> ---
command: {id: $data.result, type: $root.CMDTYPE.BUTTON, state: $data.state, eppkey: $data.eppkey }">

--- <<< Код receipt.html >>> ---
data-bind="command: {id: 'PRINT', type: CMDTYPE.BUTTON, label: {html: getLabel('GUI_[#VIEW_KEY#]_Button_Print', 'Print')}, eppkey: 1 }">
data-bind="command: {id: 'CONFIRM', type: CMDTYPE.BUTTON, label: {html: getLabel('GUI_[#VIEW_KEY#]_Button_Confirm', 'Confirm')}, eppkey: 2 }">

--- <<< Код question.html >>> ---
data-bind="command: {id: 'NO', type: CMDTYPE.BUTTON, state: CMDSTATE.ENABLED, label: {text: getLabel('GUI_[#VIEW_KEY#]_Button_No', 'No')}, eppkey: 2 }">
data-bind="command: {id: 'YES', type: CMDTYPE.BUTTON, state: CMDSTATE.ENABLED, label: {text: getLabel('GUI_[#VIEW_KEY#]_Button_Yes', 'Yes')}, eppkey: 1 }">

*/