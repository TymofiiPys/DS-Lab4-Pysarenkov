package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Scanner;

public class L4T1 {
    private final String filename;
    private ReadWriteLock rwLock;
    private Lock readLock;
    private Lock writeLock;
    private String[] allNamesAndPhones;

    public L4T1(String filename) {
        this.filename = filename;
        this.rwLock = new ReentrantReadWriteLock();
        this.readLock = this.rwLock.readLock();
        this.writeLock = this.rwLock.writeLock();
        this.fillNamesAndPhones();
    }

    public void fillNamesAndPhones() {
        allNamesAndPhones = new String[50];

        allNamesAndPhones[0] = "Іванов Іван Іванович\n+380951234567";
        allNamesAndPhones[1] = "Петров Петро Петрович\n+380961234567";
        allNamesAndPhones[2] = "Сидорова Олена Володимирівна\n+380971234567";
        allNamesAndPhones[3] = "Коваленко Андрій Миколайович\n+380981234567";
        allNamesAndPhones[4] = "Михайленко Людмила Петрівна\n+380991234567";
        allNamesAndPhones[5] = "Григоров Ігор Олександрович\n+380921234567";
        allNamesAndPhones[6] = "Ткачук Вікторія Ігорівна\n+380931234567";
        allNamesAndPhones[7] = "Шевченко Олексій Сергійович\n+380941234567";
        allNamesAndPhones[8] = "Козлов Євгенія Віталіївна\n+380951234567";
        allNamesAndPhones[9] = "Бондаренко Валентин Олександрович\n+380961234567";
        allNamesAndPhones[10] = "Мельник Лідія Василівна\n+380971234567";
        allNamesAndPhones[11] = "Герасименко Максим Володимирович\n+380981234567";
        allNamesAndPhones[12] = "Ільченко Катерина Олександрівна\n+380991234567";
        allNamesAndPhones[13] = "Павленко Віталій Юрійович\n+380921234567";
        allNamesAndPhones[14] = "Степаненко Анна Миколаївна\n+380931234567";
        allNamesAndPhones[15] = "Кравець Анастасія Ігорівна\n+380941234567";
        allNamesAndPhones[16] = "Федоров Сергій Петрович\n+380951234567";
        allNamesAndPhones[17] = "Лисенко Тетяна Вікторівна\n+380961234567";
        allNamesAndPhones[18] = "Мартиненко Валерій Михайлович\n+380971234567";
        allNamesAndPhones[19] = "Кузнецова Ірина Анатоліївна\n+380981234567";
        allNamesAndPhones[20] = "Жуков Олександр Олександрович\n+380991234567";
        allNamesAndPhones[21] = "Полякова Олена Сергіївна\n+380921234567";
        allNamesAndPhones[22] = "Кочерга Денис Володимирович\n+380931234567";
        allNamesAndPhones[23] = "Головко Ксенія Василівна\n+380941234567";
        allNamesAndPhones[24] = "Андрієнко Андрій Олегович\n+380951234567";
        allNamesAndPhones[25] = "Шульга Лариса Іванівна\n+380961234567";
        allNamesAndPhones[26] = "Даниленко Віталій Олексійович\n+380971234567";
        allNamesAndPhones[27] = "Савченко Оксана Петрівна\n+380981234567";
        allNamesAndPhones[28] = "Павлюченко Євген Вікторович\n+380991234567";
        allNamesAndPhones[29] = "Москаленко Олена Миколаївна\n+380921234567";
        allNamesAndPhones[30] = "Короткий Максим Олександрович\n+380931234567";
        allNamesAndPhones[31] = "Васильєва Тетяна Валентинівна\n+380941234567";
        allNamesAndPhones[32] = "Сергієнко Дмитро Ігорович\n+380951234567";
        allNamesAndPhones[33] = "Попова Людмила Олегівна\n+380961234567";
        allNamesAndPhones[34] = "Горбачова Анна Олександрівна\n+380971234567";
        allNamesAndPhones[35] = "Руденко Владислав Віталійович\n+380981234567";
        allNamesAndPhones[36] = "Кудряшова Юлія Іванівна\n+380991234567";
        allNamesAndPhones[37] = "Дубровіна Анастасія Валеріївна\n+380921234567";
        allNamesAndPhones[38] = "Котляр Ігор Ігорович\n+380931234567";
        allNamesAndPhones[39] = "Шаповалова Марія Петрівна\n+380941234567";
        allNamesAndPhones[40] = "Нестеренко Денис Олександрович\n+380951234567";
        allNamesAndPhones[41] = "Кучеренко Вікторія Олегівна\n+380961234567";
        allNamesAndPhones[42] = "Ігнатенко Ігор Васильович\n+380971234567";
        allNamesAndPhones[43] = "Кравчук Юлія Сергіївна\n+380981234567";
        allNamesAndPhones[44] = "Литвинов Валентин Михайлович\n+380991234567";
        allNamesAndPhones[45] = "Соколова Лідія Віталіївна\n+380921234567";
        allNamesAndPhones[46] = "Журавльов Євген Євгенович\n+380931234567";
        allNamesAndPhones[47] = "Карпенко Анастасія Віталіївна\n+380941234567";
        allNamesAndPhones[48] = "Клименко Віктор Олександрович\n+380951234567";
        allNamesAndPhones[49] = "Яковенко Світлана Олександрівна\n+380961234567";
    }

    class FindPhoneReader implements Runnable {
        public FindPhoneReader() {
            new Thread(this, "Writer").start();
        }

        @Override
        public void run() {
            while (true) {
                readLock.lock();
                String PIB = allNamesAndPhones[new Random().nextInt(allNamesAndPhones.length)];
                PIB = PIB.substring(0, PIB.indexOf('\n'));
                try (FileReader fr = new FileReader(filename)) {
                    int c;
                    int n = 0;
                    String currentPIB = "";
                    String currentPhone = "";
                    boolean found = false;
                    while ((c = fr.read()) != -1) {
                        if ((char) c == '\n') {
                            if (n % 2 == 1 && currentPIB.equals(PIB)) {
                                System.out.println("Номер телефону людини з ПІБ " + currentPIB + " - " + currentPhone);
                                found = true;
                                break;
                            }
                            n++;
                            if (n % 2 == 0) {
                                currentPIB = "";
                                currentPhone = "";
                            }
                            continue;
                        }
                        if (n % 2 == 0) {
                            currentPIB += (char) c;
                        } else {
                            currentPhone += (char) c;
                        }
                    }
                    if (!found) {
                        System.out.println("Людини з ПІБ " + PIB + " не знайдено!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                readLock.unlock();
                try {
                    Thread.sleep((new Random().nextInt(4) + 1) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class FindPIBReader implements Runnable {
        public FindPIBReader() {
            new Thread(this, "Writer").start();
        }

        @Override
        public void run() {
            while (true) {
                readLock.lock();
                String phone = allNamesAndPhones[new Random().nextInt(allNamesAndPhones.length)];
                phone = phone.substring(phone.indexOf('\n') + 1);
                try (FileReader fr = new FileReader(filename)) {
                    int c;
                    int n = 0;
                    String currentPIB = "";
                    String currentPhone = "";
                    boolean found = false;
                    while ((c = fr.read()) != -1) {
                        if ((char) c == '\n') {
                            if (n % 2 == 1 && currentPhone.equals(phone)) {
                                System.out.println("ПІБ людини з номером телефону " + currentPhone + " - " + currentPIB);
                                found = true;
                                break;
                            }
                            n++;
                            if (n % 2 == 0) {
                                currentPIB = "";
                                currentPhone = "";
                            }
                            continue;
                        }
                        if (n % 2 == 0) {
                            currentPIB += (char) c;
                        } else {
                            currentPhone += (char) c;
                        }
                    }
                    if (!found) {
                        System.out.println("Людини з телефоном " + phone + " не знайдено!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                readLock.unlock();
                try {
                    Thread.sleep((new Random().nextInt(4) + 1) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class AddRemoveEntriesWriter implements Runnable {
        public AddRemoveEntriesWriter() {
            new Thread(this, "Writer").start();
        }

        @Override
        public void run() {
            while (true) {
                writeLock.lock();
                int action = new Random().nextInt(2);
                String entry = "";
                switch (action) {
                    case 0://Додати
                        entry = allNamesAndPhones[new Random().nextInt(allNamesAndPhones.length)];
                        try (FileWriter fw = new FileWriter(filename, true)) {
                            fw.write((int) '\n');
                            System.out.print("Додано запис: ");
                            for (int i = 0; i < entry.length(); i++) {
                                fw.write((int) entry.charAt(i));
                                if (entry.charAt(i) == '\n')
                                    System.out.print(" ");
                                else
                                    System.out.print(entry.charAt(i));
                            }
                            System.out.print('\n');
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1://Видалити
                        int n = 0;
                        try (FileReader f = new FileReader(filename)) {
                            int c;
                            while ((c = f.read()) != -1) {
                                if ((char) c == '\n') {
                                    n++;
                                }
                            }
                            n++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int entryNumToDelete = new Random().nextInt(n / 2);
                        try (FileReader fr = new FileReader(filename);
                             FileWriter fw = new FileWriter("temp.txt")) {
                            int c;
                            n = 0;
                            while ((c = fr.read()) != -1) {
                                if (n / 2 != entryNumToDelete) {
                                    fw.write(c);
                                } else {
                                    if ((char) c == '\n') {
                                        entry += " ";
                                    } else {
                                        entry += (char) c;
                                    }
                                }
                                if ((char) c == '\n') {
                                    n++;
                                }
                            }
                            System.out.println("Видалено запис: " + entry);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        File f1 = new File("temp.txt");
                        File f2 = new File("database.txt");
                        f2.delete();
                        f1.renameTo(f2);
                        break;
                }

                writeLock.unlock();

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void start() {
        new AddRemoveEntriesWriter();
        new FindPhoneReader();
        new FindPIBReader();
    }

    public static void main(String[] args) {
        L4T1 l = new L4T1("database.txt");
        l.start();
    }
}