#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <ctype.h>

#define MAX_BUFFER 1000

jbyte key1_1 = 8;
jbyte key1_2 = 7;
jbyte key1_3 = 8;
jbyte key1_4 = 6;
jbyte key1_5 = 5;
jbyte key1_6 = 2;
jbyte key1_7 = 4;
jbyte key1_8 = 5;
jbyte key1_9 = 7;

jbyte key2_1 = 3;
jbyte key2_2 = 8;
jbyte key2_3 = 2;
jbyte key2_4 = 4;
jbyte key2_5 = 7;
jbyte key2_6 = 7;
jbyte key2_7 = 8;
jbyte key2_8 = 2;
jbyte key2_9 = 5;

const char *kzTens[] = {
	"", "on", "zhyrma", "otyz", "qyryq",
	"elu", "alpys", "zhetpis", "seksen", "toqsan"
};
const char *kzUnits[] = {
	"", "bir", "eki", "ush", "tort",
	"bes", "alty", "zheti", "segiz", "toghyz", "on"
};

const char *ruHundreds[] = {
	"", "sto", "dvesti", "trista", "chetyresta",
	"pyatsot", "shestsot", "semsot", "vosemsot", "devyatsot"
};
const char *ruTens[] = {
	"", "desyat", "dvadtsat", "tridtsat", "sorok",
	"pyatdesyat", "shestdesyat", "semdesyat", "vosemdesyat", "devyanosto"
};
const char *ruUnits[] = {
	"", "odin", "dva", "tri", "chetyre",
	"pyat", "shest", "sem", "vosem", "devyat",
	"desyat", "odinnadtsat", "dvenadtsat", "trinadtsat", "chetyrnadtsat",
	"pyatnadtsat", "shestnadtsat", "semnadtsat", "vosemnadtsat", "devyatnadtsat"
};

const char *enTens[] = {
	"", "ten", "twenty", "thirty", "forty",
	"fifty", "sixty", "seventy", "eighty", "ninety"
};
const char *enUnits[] = {
	"", "one", "two", "three", "four",
	"five", "six", "seven", "eight", "nine",
	"ten", "eleven", "twelve", "thirteen", "fourteen",
	"fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
};

char *convertToKazakh(int number) {
	if (number == 0) {
		char *result = malloc(sizeof(char) * 4);
		strcpy(result, "nol");
		return result;
	}
	if (number < 0) {
		char *converted = convertToKazakh(-number);
		char *result = malloc(strlen(converted) + 7); // "minus " + null terminator
		strcpy(result, "minus ");
		strcat(result, converted);
		free(converted);
		return result;
	}

	char *result = malloc(sizeof(char) * 100); // Allocate memory for the result

	// Clear the memory for the result
	memset(result, 0, 100);

	if (number / 1000000 > 0) {
		char *temp = convertToKazakh(number / 1000000);
		strcat(result, temp);
		free(temp);
		strcat(result, "million ");
		number %= 1000000;
	}


	if (number / 1000 > 0) {
		char *temp = convertToKazakh(number / 1000);
		strcat(result, temp);
		free(temp);
		strcat(result, "myn ");
		number %= 1000;
	}

	if (number / 100 > 0) {
		char *temp = convertToKazakh(number / 100);
		strcat(result, temp);
		free(temp);
		strcat(result, "zhuz ");
		number %= 100;
	}

	if (number >= 10) {
		strcat(result, kzTens[(int) number / 10]);
		strcat(result, " ");
		number %= 10;
	}

	if (number > 0) {
		strcat(result, kzUnits[(int) number]);
		strcat(result, " ");
	}

	return result;
}

int chkRu(int exp1, int exp2) {
	if (exp1 == 11 || exp1 == 12 || exp1 == 13
			|| exp1 == 14 || exp1 == 15 || exp1 == 16
			|| exp1 == 17 || exp1 == 18 || exp1 == 19
			|| exp1 % 100 == 11 || exp1 % 100 == 12 || exp1 % 100 == 13
			|| exp1 % 100 == 14 || exp1 % 100 == 15 || exp1 % 100 == 16
			|| exp1 % 100 == 17 || exp1 % 100 == 18 || exp1 % 100 == 19) {
		exp2 = 0;
	}
	return exp2;
}

char *convertToRussian(int number) {
	if (number == 0) {
		char *result = malloc(sizeof(char) * 4);
		strcpy(result, "nol");
		return result;
	}
	if (number < 0) {
		char *converted = convertToRussian(-number);
		char *result = malloc(strlen(converted) + 7); // "minus " + null terminator
		strcpy(result, "minus ");
		strcat(result, converted);
		free(converted);
		return result;
	}

	char *result = malloc(sizeof(char) * 100); // Allocate memory for the result

	// Clear the memory for the result
	memset(result, 0, 100);

	int exp1 = number / 1000000;
	if (exp1 > 0) {
		int exp2 = exp1 % 10;
		exp2 = chkRu(exp1, exp2);
		if (exp2 == 1) {
			char *temp = convertToRussian(number / 1000000);
			strcat(result, temp);
			free(temp);
			strcat(result, "million ");
			number %= 1000000;
		} else if (exp2 == 2 || exp2 == 3 || exp2 == 4) {
			char *temp = convertToRussian(number / 1000000);
			strcat(result, temp);
			free(temp);
			strcat(result, "milliona ");
			number %= 1000000;
		} else {
			char *temp = convertToRussian(number / 1000000);
			strcat(result, temp);
			free(temp);
			strcat(result, "millionov ");
			number %= 1000000;
		}
	}

	int exp3 = number / 1000;
	if (exp3 > 0) {
		int exp4 = exp3 % 10;
		exp4 = chkRu(exp3, exp4);
		if (exp4 == 1) {
			char *temp = convertToRussian(exp3);
			strcat(result, temp);
			free(temp);
			result[strlen(result) - 5] = '\0';
			strcat(result, "odna tysyacha ");
		} else if (exp4 == 2) {
			char *temp = convertToRussian(exp3);
			strcat(result, temp);
			free(temp);
			result[strlen(result) - 4] = '\0';
			strcat(result, "dve tysyachi ");
		} else if (exp4 == 3 || exp4 == 4) {
			char *temp = convertToRussian(exp3);
			strcat(result, temp);
			free(temp);
			strcat(result, "tysyachi ");
		} else {
			char *temp = convertToRussian(exp3);
			strcat(result, temp);
			free(temp);
			strcat(result, "tysyach ");
		}
		number %= 1000;
	}

	if (number / 100 > 0) {
		strcat(result, ruHundreds[(int) number / 100]);
		strcat(result, " ");
		number %= 100;
	}

	if (number >= 20) {
		strcat(result, ruTens[(int) number / 10]);
		strcat(result, " ");
		number %= 10;
	}

	if (number > 0) {
		strcat(result, ruUnits[(int) number]);
		strcat(result, " ");
	}

	return result;
}

char *convertToEnglish(int number) {
	if (number == 0) {
		char *result = malloc(sizeof(char) * 5);
		strcpy(result, "zero");
		return result;
	}
	if (number < 0) {
		char *converted = convertToEnglish(-number);
		char *result = malloc(strlen(converted) + 7); // "minus " + null terminator
		strcpy(result, "minus ");
		strcat(result, converted);
		free(converted);
		return result;
	}

	char *result = malloc(sizeof(char) * 100); // Allocate memory for the result

	// Clear the memory for the result
	memset(result, 0, 100);

	if (number / 1000000 > 0) {
		char *temp = convertToEnglish(number / 1000000);
		strcat(result, temp);
		free(temp);
		strcat(result, "million ");
		number %= 1000000;
	}


	if (number / 1000 > 0) {
		char *temp = convertToEnglish(number / 1000);
		strcat(result, temp);
		free(temp);
		strcat(result, "thousand ");
		number %= 1000;
	}

	if (number / 100 > 0) {
		char *temp = convertToEnglish(number / 100);
		strcat(result, temp);
		free(temp);
		strcat(result, "hundred ");
		number %= 100;
	}

	if (number >= 20) {
		strcat(result, enTens[(int) number / 10]);
		strcat(result, " ");
		number %= 10;
	}

	if (number > 0) {
		strcat(result, enUnits[(int) number]);
		strcat(result, " ");
	}

	return result;
}

// Функция шифрования строки с использованием XOR
void encryptString1(char* str) {
    int length = strlen(str);
    for (int i = 0; i < length; i++) {
		if (i % 9 == 0) {
			str[i] = str[i] ^ key1_9;
		} else if (i % 8 == 0) {
			str[i] = str[i] ^ key1_8;
		} else if (i % 7 == 0) {
			str[i] = str[i] ^ key1_7;
		} else if (i % 6 == 0) {
			str[i] = str[i] ^ key1_6;
		} else if (i % 5 == 0) {
			str[i] = str[i] ^ key1_5;
		} else if (i % 4 == 0) {
			str[i] = str[i] ^ key1_4;
		} else if (i % 3 == 0) {
			str[i] = str[i] ^ key1_3;
		} else if (i % 2 == 0) {
			str[i] = str[i] ^ key1_2;
		} else {
			str[i] = str[i] ^ key1_1;
		}
    }
}

// Функция шифрования строки с использованием XOR
void encryptString2(char* str) {
    int length = strlen(str);
    for (int i = 0; i < length; i++) {
		if (i % 9 == 0) {
			str[i] = str[i] ^ key2_9;
		} else if (i % 8 == 0) {
			str[i] = str[i] ^ key2_8;
		} else if (i % 7 == 0) {
			str[i] = str[i] ^ key2_7;
		} else if (i % 6 == 0) {
			str[i] = str[i] ^ key2_6;
		} else if (i % 5 == 0) {
			str[i] = str[i] ^ key2_5;
		} else if (i % 4 == 0) {
			str[i] = str[i] ^ key2_4;
		} else if (i % 3 == 0) {
			str[i] = str[i] ^ key2_3;
		} else if (i % 2 == 0) {
			str[i] = str[i] ^ key2_2;
		} else {
			str[i] = str[i] ^ key2_1;
		}
    }
}

char *executeCommand(const char *command) {
    FILE *fp;
    char *output = (char *)malloc(sizeof(char) * MAX_BUFFER);
    if (!output) {
        fprintf(stderr, "Memory allocation failed.\n");
        exit(EXIT_FAILURE);
    }

    fp = popen(command, "r");
    if (fp == NULL) {
        fprintf(stderr, "Failed to run command\n");
        exit(EXIT_FAILURE);
    }

    // Игнорируем заголовок
    fgets(output, MAX_BUFFER, fp);

    // Считываем фактическое значение
    fgets(output, MAX_BUFFER, fp);

    pclose(fp);
    output[strcspn(output, "\n")] = 0; // Удаление символа новой строки

    // Убираем пробелы и другие нежелательные символы в начале и в конце строки
    char *trimmedOutput = output;
    while (isspace((unsigned char)*trimmedOutput)) {
        trimmedOutput++;
    }
    int len = strlen(trimmedOutput);
    while (len > 0 && isspace((unsigned char)trimmedOutput[len - 1])) {
        trimmedOutput[--len] = 0;
    }

    return trimmedOutput;
}

char *getString() {
    char *computersystemName = executeCommand("hostname");
    char *baseboardSerial = executeCommand("sudo dmidecode -s baseboard-serial-number");
    char *diskDriveSerial = executeCommand("sudo dmidecode -s baseboard-serial-number");

    // Удаление символа новой строки из строк
	computersystemName[strcspn(computersystemName, "\n")] = '\0';
    baseboardSerial[strcspn(baseboardSerial, "\n")] = '\0';
    diskDriveSerial[strcspn(diskDriveSerial, "\n")] = '\0';

    // Обрезка или дополнение нулями для фиксированной длины 20 символов
    int computersystemLen = strlen(computersystemName);
    int baseboardLen = strlen(baseboardSerial);
    int diskDriveLen = strlen(diskDriveSerial);

	if (computersystemLen > 20) {
        computersystemName[20] = '\0'; // Обрезаем до 20 символов
        computersystemLen = 20;
    } else {
        while (computersystemLen < 20) {
            strcat(computersystemName, "_"); // Добавляем подчеркивания до 20 символов
            computersystemLen++;
        }
    }

    if (baseboardLen > 20) {
        baseboardSerial[20] = '\0'; // Обрезаем до 20 символов
        baseboardLen = 20;
    } else {
        while (baseboardLen < 20) {
            strcat(baseboardSerial, "_"); // Добавляем подчеркивания до 20 символов
            baseboardLen++;
        }
    }

    if (diskDriveLen > 20) {
        diskDriveSerial[20] = '\0'; // Обрезаем до 20 символов
        diskDriveLen = 20;
    } else {
        while (diskDriveLen < 20) {
            strcat(diskDriveSerial, "_"); // Добавляем подчеркивания до 20 символов
            diskDriveLen++;
        }
    }

    // Выделение памяти под объединенную строку
    char *combinedSerial = (char *)malloc(sizeof(char) * 61); // 60 символов + завершающий нуль
    if (!combinedSerial) {
        fprintf(stderr, "Memory allocation failed.\n");
        exit(EXIT_FAILURE);
    }

    // Формирование строки вида: baseboardSerial_diskDriveSerial
    sprintf(combinedSerial, "%.20s%.20s%.20s", computersystemName, baseboardSerial, diskDriveSerial);

    // Освобождение памяти
    free(computersystemName);
    free(baseboardSerial);
    free(diskDriveSerial);

    return combinedSerial;
}

JNIEXPORT jstring JNICALL Java_com_example_nts_service_JNIService_convertToRussian(JNIEnv *env, jobject obj, jint number) {
	char buffer[1024] = { 0 };

	// Чтение файла на уровне JNI
    FILE *file = fopen("bs2kz.v2c", "r");
    if (file != NULL) {
        if (fgets(buffer, sizeof(buffer), file) != NULL) {
			encryptString1(buffer);
        }
        fclose(file);
    }

	// Сравнение строк
    if (strcmp(buffer, getString()) == 0) {
		char *result = convertToRussian(number);
		jstring jResult = (*env)->NewStringUTF(env, result);
		free(result); // Освобождаем память после использования
		return jResult;
    } else {
		// Открыть файл для записи
		FILE *file = fopen("bs2kz.c2v", "w");
		if (file != NULL) {
			char *string = getString();
			encryptString2(string);
			// Записать слово в файл
			fprintf(file, string);
			// Закрыть файл
			fclose(file);
		}

		return NULL;
	}
}

JNIEXPORT jstring JNICALL Java_com_example_nts_service_JNIService_convertToKazakh(JNIEnv *env, jobject obj, jint number) {
	char buffer[1024] = { 0 };

	// Чтение файла на уровне JNI
    FILE *file = fopen("bs2kz.v2c", "r");
    if (file != NULL) {
        if (fgets(buffer, sizeof(buffer), file) != NULL) {
			encryptString1(buffer);
        }
        fclose(file);
    }

	// Сравнение строк
    if (strcmp(buffer, getString()) == 0) {
		char *result = convertToKazakh(number);
		jstring jResult = (*env)->NewStringUTF(env, result);
		free(result); // Освобождаем память после использования
		return jResult;
    } else {
		// Открыть файл для записи
		FILE *file = fopen("bs2kz.c2v", "w");
		if (file != NULL) {
			char *string = getString();
			encryptString2(string);
			// Записать слово в файл
			fprintf(file, string);
			// Закрыть файл
			fclose(file);
		}

		return NULL;
	}
}

JNIEXPORT jstring JNICALL Java_com_example_nts_service_JNIService_convertToEnglish(JNIEnv *env, jobject obj, jint number) {
	char buffer[1024] = { 0 };

	// Чтение файла на уровне JNI
    FILE *file = fopen("bs2kz.v2c", "r");
    if (file != NULL) {
        if (fgets(buffer, sizeof(buffer), file) != NULL) {
			encryptString1(buffer);
        }
        fclose(file);
    }

	// Сравнение строк
    if (strcmp(buffer, getString()) == 0) {
		char *result = convertToEnglish(number);
		jstring jResult = (*env)->NewStringUTF(env, result);
		free(result); // Освобождаем память после использования
		return jResult;
    } else {
		// Открыть файл для записи
		FILE *file = fopen("bs2kz.c2v", "w");
		if (file != NULL) {
			char *string = getString();
			encryptString2(string);
			// Записать слово в файл
			fprintf(file, string);
			// Закрыть файл
			fclose(file);
		}

		return NULL;
	}
}