import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

import ru.textanalysis.tawt.jmorfsdk.JMorfSdk;
import ru.textanalysis.tawt.jmorfsdk.loader.JMorfSdkFactory;
import ru.textanalysis.tawt.graphematic.parser.text.GParserImpl;
import ru.textanalysis.tawt.graphematic.parser.text.GraphematicParser;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters;
import ru.textanalysis.tawt.ms.internal.IOmoForm;

public class PracticMain {

    private final static int ARG = 1;
    private final static int ARGS = 2;
    private final static String YES = "YES";
    private final static String SHORT_YES = "Y";
    private final static String NO = "NO";
    private final static String SHORT_NO = "N";

    public static void main(String[] args) throws Exception {

        List<List<String>> ParsingText;
        List<IOmoForm> characteristicList = null;
        List<String> forms = null;
        StringBuilder textBuilder = new StringBuilder();
        String s;
        String text = null;
        Scanner input = new Scanner(System.in);
        if (args.length == ARGS
                && (args[0].equalsIgnoreCase("-f") || args[0].equalsIgnoreCase("--file"))) {
            try (BufferedReader bufReader = new BufferedReader(new FileReader(args[1]))) {
                while ((s = bufReader.readLine()) != null) {
                    textBuilder.append(s);
                }
                text = textBuilder.toString();
            } catch (IOException e) {
                System.out.println("Файл не найден.");
            }
        } else if (args.length == ARG) {
            try (BufferedReader bufReader = new BufferedReader(new FileReader(args[1]))) {
                while ((s = bufReader.readLine()) != null) {
                    textBuilder.append(s);
                }
                text = textBuilder.toString();
            } catch (IOException e) {
                System.out.println("Файл не найден.");
            }
        } else {
            System.out.println("Ввести путь до файла вручную? [yes]/[no]");
            String choose = input.nextLine();
            if (choose.toUpperCase().equals(YES) || choose.toUpperCase().equals(SHORT_YES)) {
                System.out.println("Введите путь до файла:");
                String filePath = input.nextLine();
                boolean flag = true;
                while (true) {
                    flag = true;
                    try (BufferedReader bufReader = new BufferedReader(new FileReader(filePath))) {
                        while ((s = bufReader.readLine()) != null) {
                            textBuilder.append(s);
                            flag = false;
                        }
                        text = textBuilder.toString();
                    } catch (IOException e) {
                        System.out.println("Файл не найден, попробуйте еще раз");
                        flag = true;
                    }
                }
            } else if (choose.toUpperCase().equals(NO) || choose.toUpperCase().equals(SHORT_NO)) {
                System.out.println("Введите текст вручную:");
                boolean flag = true;
                while (flag) {
                    s = input.nextLine();
                    if (!s.equals("")) {
                        textBuilder.append(s).append("\n");
                        flag = true;
                    } else {
                        flag = false;
                    }
                    text = textBuilder.toString();
                }
            } else {
                System.out.println("Некорректный ввод");
                return;
            }
        }
        System.out.println("Исходный текст: " + text);

        GraphematicParser parser = new GParserImpl();
        List<List<String>> listBasicPhase = parser.parserSentence(text);
        System.out.println("Слова после парсинга:\n");
        for (List<String> words : listBasicPhase) {
            for (String word : words) {
                System.out.println(word);
            }
        }
        JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();
        for (List<String> words : listBasicPhase) {
            for (String word : words) {
                System.out.println("_________________________________________________");
                System.out.println("Получение словоформ для слова \"" + word + "\"");
                characteristicList = jMorfSdk.getAllCharacteristicsOfForm(word);
                jMorfSdk.getAllCharacteristicsOfForm(word).forEach(form -> {
                    System.out.println(form);
                    if (form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.NOUN) {
                        System.out.println("слово \"" + word + "\" - существительное");
                        genderPrint(word, form);
                        casePrint(word, form);
                        numbersPrint(word, form);
                    } else if (form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.ADVERB) {
                        System.out.println("слово \"" + word + "\" - наречие");
                    } else if (form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.ADJECTIVEFULL) {
                        System.out.println("слово \"" + word + "\" - полное прилагательное");
                        genderPrint(word, form);
                        casePrint(word, form);
                        numbersPrint(word, form);
                    } else if (form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.ADJECTIVESHORT) {
                        System.out.println("слово \"" + word + "\" - краткое прилагательное");
                        genderPrint(word, form);
                        casePrint(word, form);
                        numbersPrint(word, form);
                    } else if (form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.VERB) {
                        System.out.println("слово \"" + word + "\" - глагол");
                        genderPrint(word, form);
                        if (form.getTheMorfCharacteristics(MorfologyParameters.Time.class) == MorfologyParameters.Time.FUTURE) {
                            System.out.println("слово \"" + word + "\" - будущего времени");
                        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Time.class) == MorfologyParameters.Time.PAST) {
                            System.out.println("слово \"" + word + "\" - прошедшего времени");
                        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Time.class) == MorfologyParameters.Time.PRESENT) {
                            System.out.println("слово \"" + word + "\" - настоящего");
                        }
                        numbersPrint(word, form);
                    } else if (form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.INFINITIVE) {
                        System.out.println("слово \"" + word + "\" - инфинитив");
                    } else if (form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.NUMERAL) {
                        System.out.println("слово \"" + word + "\" - числительное");
                        genderPrint(word, form);
                        casePrint(word, form);
                        numbersPrint(word, form);
                    } else if (form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.COMPARATIVE) {
                        System.out.println("слово \"" + word + "\" - сравнение");
                    } else if (form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.UNION) {
                        System.out.println("слово \"" + word + "\" - союз");
                    }
                });
            }
        }

        System.out.println("Изменение всех возможных слов текста на существительные в именительном падеже:");
        for (List<String> words : listBasicPhase) {
            for (String word : words) {
                jMorfSdk.getAllCharacteristicsOfForm(word).forEach((form) -> {
                    if ((form.getTheMorfCharacteristics(MorfologyParameters.Case.IDENTIFIER) == MorfologyParameters.Case.NOMINATIVE) && form.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.NOUN) {
                        System.out.println("изменение слова \"" + word + "\"");
                        System.out.println(form.getInitialFormString());
                    }
                });
            }
        }
    }

    private static void genderPrint(String word, IOmoForm form) {
        if (form.getTheMorfCharacteristics(MorfologyParameters.Gender.class) == MorfologyParameters.Gender.FEMININ) {
            System.out.println("слово \"" + word + "\" - женского рода");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Gender.class) == MorfologyParameters.Gender.MANS) {
            System.out.println("слово \"" + word + "\" - мужского рода");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Gender.class) == MorfologyParameters.Gender.NEUTER) {
            System.out.println("слово \"" + word + "\" - среднего рода");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Gender.class) == MorfologyParameters.Gender.UNCLEARGENDER) {
            System.out.println("слово \"" + word + "\" - неизвестного рода");
        }
    }

    private static void casePrint(String word, IOmoForm form) {
        if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.ABLTIVE) {
            System.out.println("слово \"" + word + "\" - творительный падеж");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.ACCUSATIVE) {
            System.out.println("слово \"" + word + "\" - винительный падеж");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.ACCUSATIVE2) {
            System.out.println("слово \"" + word + "\" - винительный падеж");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.DATIVE) {
            System.out.println("слово \"" + word + "\" - дательный падеж");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.GENITIVE) {
            System.out.println("слово \"" + word + "\" - родительный падеж");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.GENITIVE1) {
            System.out.println("слово \"" + word + "\" - родительный падеж");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.GENITIVE2) {
            System.out.println("слово \"" + word + "\" - родительный падеж");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.NOMINATIVE) {
            System.out.println("слово \"" + word + "\" - именительный падеж");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.PREPOSITIONA) {
            System.out.println("слово \"" + word + "\" - предлог");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.VOATIVE) {
            System.out.println("слово \"" + word + "\" - звательный падеж");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.PREPOSITIONA1) {
            System.out.println("слово \"" + word + "\" - предлог");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Case.class) == MorfologyParameters.Case.PREPOSITIONA2) {
            System.out.println("слово \"" + word + "\" - предлог");
        }
    }

    private static void numbersPrint(String word, IOmoForm form) {
        if (form.getTheMorfCharacteristics(MorfologyParameters.Numbers.class) == MorfologyParameters.Numbers.SINGULAR) {
            System.out.println("слово \"" + word + "\" - единственного числа");
        } else if (form.getTheMorfCharacteristics(MorfologyParameters.Numbers.class) == MorfologyParameters.Numbers.PLURAL) {
            System.out.println("слово \"" + word + "\" - множественного числа");
        }
    }
}
