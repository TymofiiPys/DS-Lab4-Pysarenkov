package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

public class L4T2 {
    class Garden {
        /**
         * Масив, що позначає сад. Кожен елемент позначає клумбу і має
         * 4 значення:
         * 0 - квітки немає у клумбі
         * 1 - квітка у клумбі зів'яла
         * 2-4 - квітка у клумбі скоро зів'яне/нормальний стан/недавно полита, але висохла
         * 5 - квітка у клумбі полита
         */
        private int[][] garden;
        private final int size = 10;
        private int nr = 0;

        public Garden() {
            garden = new int[size][size];
            fillWithRandom();
        }

        private void fillWithRandom() {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (new Random().nextInt(3) != 0) {
                        garden[i][j] = 0;
                    } else {
                        garden[i][j] = new Random().nextInt(6);
                    }
                }
            }
        }

        private synchronized void startRead() {
            nr++;
        }

        private synchronized void endRead() {
            nr--;
            if (nr == 0) {
                notify();
            }
        }

        public void read(String threadInvoker) {
            startRead();
            switch (threadInvoker) {
                case "FileOutputReader":
                    try (FileWriter fw = new FileWriter("garden.txt", true)) {
                        String time = LocalDateTime.now().toString();
                        fw.append('\n');
                        for (int i = 0; i < time.length(); i++) {
                            fw.append(time.charAt(i));
                        }
                        fw.append('\n');
                        for (int i = 0; i < size; i++) {
                            String gardenRow = Arrays.toString(garden[i]);
                            for (int j = 0; j < gardenRow.length(); j++) {
                                fw.append(gardenRow.charAt(j));
                            }
                            fw.append('\n');
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                case "ConsoleOutputReader":
                    System.out.println();
                    for (int i = 0; i < size; i++) {
                        System.out.println(Arrays.toString(garden[i]));
                    }
            }
            endRead();
        }

        public synchronized void write(String threadInvoker) {
            while (nr > 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            switch (threadInvoker) {
                case "GardenerWriter":
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            switch (garden[i][j]) {
                                case 0 -> {
                                    int plantOrNot = new Random().nextInt(8);
                                    if (plantOrNot == 0) {
                                        garden[i][j] = 5;
                                    }
                                }
                                case 1 -> {
                                    garden[i][j] = 0;
                                }
                            }
                            if (garden[i][j] >= 2 && garden[i][j] < 5) {
                                int waterOrNot = new Random().nextInt(3);
                                if (waterOrNot == 0) {
                                    garden[i][j] = 5;
                                }
                            }
                        }
                    }
                case "NatureWriter":
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            if (garden[i][j] > 1) {
                                garden[i][j]--;
                            }
                        }
                    }
            }
            notify();
        }
    }

    class GardenerWriter implements Runnable {
        Garden g;

        public GardenerWriter(Garden g) {
            this.g = g;
            new Thread(this, "GardenerWriter").start();
        }

        @Override
        public void run() {
            while (true) {
                g.write(Thread.currentThread().getName());
                try {
                    Thread.sleep((new Random().nextInt(3) + 4) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class NatureWriter implements Runnable {
        Garden g;

        public NatureWriter(Garden g) {
            this.g = g;
            new Thread(this, "NatureWriter").start();
        }

        @Override
        public void run() {
            while (true) {
                g.write(Thread.currentThread().getName());
                try {
                    Thread.sleep((new Random().nextInt(3) + 2) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class FileOutputReader implements Runnable {
        Garden g;

        public FileOutputReader(Garden g) {
            this.g = g;
            new Thread(this, "FileOutputReader").start();
        }

        @Override
        public void run() {
            while (true) {
                g.read(Thread.currentThread().getName());
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class ConsoleOutputReader implements Runnable {
        Garden g;

        public ConsoleOutputReader(Garden g) {
            this.g = g;
            new Thread(this, "ConsoleOutputReader").start();
        }

        @Override
        public void run() {
            while (true) {
                g.read(Thread.currentThread().getName());
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void start() {
        Garden g = new Garden();
        new NatureWriter(g);
        new GardenerWriter(g);
        new FileOutputReader(g);
        new ConsoleOutputReader(g);
    }

    public static void main(String[] args) {
        L4T2 l = new L4T2();
        l.start();
    }
}
