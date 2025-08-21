var tempLanguage = '';
var tempAmount = '';
var tempScreenID = '';
var tempTotal = '';
var tempBalance = '';
var tempMessageAmount = '';
var tempMessageAmountMax = '';
var tempMessageAmountMin = '';

var screensArray = [];

var language = '';
var amountFirstPlayed = false;
var lastExecutionTime = Date.now();
var countOnFirstScreen = 0;

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

        // Воспроизводим все, кроме тех, что в массиве screensArray
		if (screenID !== tempScreenID && !screensArray.includes(screenID)) {
            if (screenID === 'IdleLoopPresentation' && countOnFirstScreen <= 6) { // 3 секунды ожидания на "IdleLoopPresentation" экране (500 * 6 = 3000ms)
                countOnFirstScreen++;
                return;
            }

			await playbackScreen(screenID, language); // Воспроизводим аудио файл(ы)
			tempScreenID = screenID; // Обновляем tempScreenID
			// Сброс параметров, чтобы при смене экрана и возвращении на него, аудио воспроизводилось сначала
            tempMessageAmount = '';
            tempMessageAmountMax = '';
            tempMessageAmountMin = '';
            amountFirstPlayed = false;
            countOnFirstScreen = 0;
		}

		// Получаем значение элемента generalInput для озвучки вводимой с клавиатуры суммы (screen 031, ...)
		let inputField = iframeDoc.getElementById('generalInput');
		if (inputField) {
			// Очищаем поле для ввода суммы от двух первых символов '₸ '
			let amount = cleanAmount(inputField.value);
			if (amount !== tempAmount) {
				if (amount !== '0') {
					await playbackAmount(amount, language); // Воспроизводим сумму при каждом изменении
					amountFirstPlayed = true;
				} else if (amountFirstPlayed === true) {
					await playbackButton('buttonCorrect', language); // Воспроизводим аудио файл(ы) для нажатой кнопки
					amountFirstPlayed = false;
				}
				tempAmount = amount; // Обновляем tempAmount
			}
		}

		// Получаем значение элемента flexDepositResultTotalAmount для озвучки внесенной суммы (screen 102)
		let resultTotal = iframeDoc.getElementById('flexDepositResultTotalAmount');
		if (resultTotal) {
			// Находим все span
			let spans = resultTotal.querySelectorAll('span');
			if (!spans) console.log("spans not found");
			// Извлекаем текст второго span
			if (spans.length > 1) {
				let total = spans[1].textContent.trim();
				if (total !== tempTotal) {
					await playbackTotal(total, language); // Воспроизводим итоговую сумму
					tempTotal = total; // Обновляем tempTotal
				}
			} else {
				console.log("Total amount not found");
			}
		}

		// Получаем значение элемента MESSAGE для озвучки суммы (для разных скринов используется одинаковый id='MESSAGE')
		let message = iframeDoc.getElementById('MESSAGE');
		const outputLine = message.querySelector('span');
		if (outputLine) {
			let messageAmount = outputLine.innerText.trim();
			let match = messageAmount.match(/^([+-])?(\d+)(?:\.(\d+))?/);
			if (match) {
				let sign = match[1] || ""; // Знак: "+" или "-", если есть
				let integerPart = match[2]; // Целая часть числа
				let fractionalPart = match[3] || ""; // Дробная часть числа, если есть
				if (screenID === screen234) {
					let currentTime = Date.now(); // Текущее время
					let elapsedTime = currentTime - lastExecutionTime; // Разница во времени
					if (messageAmount !== tempBalance || elapsedTime >= 40000) {
						await playbackBalance(sign, integerPart, fractionalPart, language);
						tempBalance = messageAmount; // Обновляем tempBalance
						lastExecutionTime = Date.now(); // Обновляем время начала
					}
				} else if (screenID === screen042) {
					if (messageAmount !== tempMessageAmount) {
						await playbackMessageAmount(integerPart, fractionalPart, language);
						tempMessageAmount = messageAmount;
					}
				} else if (screenID === screen043) {
					if (messageAmount !== tempMessageAmountMax) {
						await playbackMessageAmountMax(integerPart, fractionalPart, language);
						tempMessageAmountMax = messageAmount;
					}
				} else if (screenID === screen044) {
					if (messageAmount !== tempMessageAmountMin) {
						await playbackMessageAmountMin(integerPart, fractionalPart, language);
						tempMessageAmountMin = messageAmount;
					}
				}
			} else {
				console.log("Unable to recognize the number");
			}
		}
	} else {
		// Убираем размытие
		deactivateBlur(iframeDoc);
		// Останавливаем предыдущее воспроизведение, если есть
		await stopPlayback();

		// Сбрасываем все параметры
		tempLanguage = '';
		tempAmount = '';
		tempScreenID = '';
		tempTotal = '';
		tempBalance = '';
		tempMessageAmount = '';
    	tempMessageAmountMax = '';
    	tempMessageAmountMin = '';
	}
}, 500); // Проверяем каждые 500 мс

/* ********************************************************* */

async function playbackScreen(screenID, language) {
	// Получаем список файлов для воспроизведения, соответствующих текущему экрану
	let data = await getScreenMapping(screenID, language);
	if (data?.mapping) {
		playback(data.mapping, language); // Воспроизводим файлы
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
		playback(data.mapping, language); // Воспроизводим файлы
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
	playback(audioFiles, language); // Воспроизводим сумму снятия и сопроводительные файлы
}

async function playbackTotal(total, language) {
	// Получаем аудиофайлы, сопоставленные для воспроизведения суммы пополнения
	let response = await getTotalMapping('total.txt', language);
	let audioFiles = response.mapping.map(item => {
		if (item === "$total") {
			return String(total.replace(",00", ""));
		} else {
			return item; // Оставляем элемент без изменений
		}
	});
	playback(audioFiles, language); // Воспроизводим сумму пополнения и сопроводительные файлы
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
	playback(audioFiles, language); // Воспроизводим сумму баланса и сопроводительные файлы
}

async function playbackMessageAmount(integerPart, fractionalPart, language) {
	// Получаем аудиофайлы, сопоставленные для воспроизведения суммы
	let response = await getMessageAmountMapping('message-amount.txt', language);
	let audioFiles = response.mapping.map(item => {
		if (item === "$integer") {
			return String(integerPart);
		} else if (item === "$fractional") {
			return String(fractionalPart);
		} else {
			return item; // Оставляем элемент без изменений
		}
	});
	playback(audioFiles, language); // Воспроизводим сумму и сопроводительные файлы
}

async function playbackMessageAmountMax(integerPart, fractionalPart, language) {
	// Получаем аудиофайлы, сопоставленные для воспроизведения суммы
	let response = await getMessageAmountMappingMax('message-amount-max.txt', language);
	let audioFiles = response.mapping.map(item => {
		if (item === "$integer") {
			return String(integerPart);
		} else if (item === "$fractional") {
			return String(fractionalPart);
		} else {
			return item; // Оставляем элемент без изменений
		}
	});
	playback(audioFiles, language); // Воспроизводим сумму и сопроводительные файлы
}

async function playbackMessageAmountMin(integerPart, fractionalPart, language) {
	// Получаем аудиофайлы, сопоставленные для воспроизведения суммы
	let response = await getMessageAmountMappingMin('message-amount-min.txt', language);
	let audioFiles = response.mapping.map(item => {
		if (item === "$integer") {
			return String(integerPart);
		} else if (item === "$fractional") {
			return String(fractionalPart);
		} else {
			return item; // Оставляем элемент без изменений
		}
	});
	playback(audioFiles, language); // Воспроизводим сумму и сопроводительные файлы
}

/* ********************************************************* */

function cleanAmount(amount) {
	if (/^\d/.test(amount)) {
        return amount;
    } else {
        return amount.replace(/^.{2}/, '');
    }
}

function updateLanguage(language) {
	if (language !== tempLanguage) {
		tempLanguage = language;
		tempAmount = '';
		tempScreenID = '';
		tempTotal = '';
		tempBalance = '';
		tempMessageAmount = '';
        tempMessageAmountMax = '';
        tempMessageAmountMin = '';
	}
}

function activateBlur(iframeDoc) {
  iframeDoc.body.style.filter = "blur(20px)";
  iframeDoc.body.style.pointerEvents = "none"; // блокируем клики
}

function deactivateBlur(iframeDoc) {
  iframeDoc.body.style.filter = "";
  iframeDoc.body.style.pointerEvents = "";
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

function getMessageAmountMapping(filename, language) {
	return sendPostRequest('http://localhost:8081/mapping/message-amount', { filename, language });
}

function getMessageAmountMappingMax(filename, language) {
	return sendPostRequest('http://localhost:8081/mapping/message-amount-max', { filename, language });
}

function getMessageAmountMappingMin(filename, language) {
	return sendPostRequest('http://localhost:8081/mapping/message-amount-min', { filename, language });
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