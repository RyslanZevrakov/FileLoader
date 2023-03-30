package org.example;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Security {
    private static final String FILE_NAME = "codes.txt";
    private static final int CODE_LENGTH = 6;
    private static final int EMPLOYEES_COUNT = 10;

    static class Employee {
        String name;
        String code;
        String lastName;

        Employee(String name, String lastName) {
            this.name = name;
            this.lastName = lastName;
        }

        String generateCode() {
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        }

        void writeCodeToFile() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                writer.write(name + " " + lastName + " " + code);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class CodeGenerator implements Runnable {
        List<Employee> employees;
        Map<String, String> codes;

        CodeGenerator(List<Employee> employees) {
            this.employees = employees;
            codes = new HashMap<>();
        }

        @Override
        public void run() {
            try {
                File codesFile = new File(FILE_NAME);
                if (codesFile.exists())
                    codesFile.delete();
                codesFile.createNewFile();
                BufferedReader reader = new BufferedReader(new FileReader(codesFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    codes.put(parts[0] + " " + parts[1], parts[2]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Employee employee : employees) {
                if (!codes.containsKey(employee.name + " " + employee.lastName)) {
                    employee.code = employee.generateCode();
                    codes.put(employee.name + " " + employee.lastName, employee.code);
                    employee.writeCodeToFile();
                } else {
                    employee.code = codes.get(employee.name + " " + employee.lastName);
                }
            }
        }
    }

    static class ConsoleReader implements Runnable {
        Scanner scanner;
        List<Employee> employees;

        ConsoleReader(List<Employee> employees) {
            scanner = new Scanner(System.in);
            this.employees = employees;
        }

        @Override
        public void run() {
            System.out.println("Введите номер сотрудника:");
            int index = scanner.nextInt() - 1;
            if (index < 0 || index >= employees.size()) {
                System.out.println("Неверный номер сотрудника");
                return;
            }
            Employee employee = employees.get(index);
            System.out.println("Загрузка данных...0%");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Загрузка данных...100%");
            System.out.println("Сотрудник - " + employee.lastName + ", код доступа - " + employee.code);
        }
    }

    static class CodeRequester implements Runnable {
        Scanner scanner;
        List<Employee> employees;

        CodeRequester(List<Employee> employees) {
            scanner = new Scanner(System.in);
            this.employees = employees;
        }

        @Override
        public void run() {
            System.out.println("Генерация кода доступа...0%");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Генерация кода доступа...100%");
            System.out.println("Введите номер сотрудника для запроса кода:");
            int index = scanner.nextInt() - 1;
            if (index < 0 || index >= employees.size()) {
                System.out.println("Неверный номер сотрудника");
                return;
            }
            Employee employee = employees.get(index);
            System.out.println("Сотрудник - " + employee.lastName);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            employee.code = employee.generateCode();
            employee.writeCodeToFile();
            System.out.println("Код доступа - " + employee.code);
        }
    }

    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("Иван", "Иванов"));
        employees.add(new Employee("Петр", "Петров"));
        employees.add(new Employee("Сидор", "Сидоров"));
        employees.add(new Employee("Николай", "Николаев"));
        employees.add(new Employee("Александр", "Александров"));
        employees.add(new Employee("Владимир", "Владимиров"));
        employees.add(new Employee("Дмитрий", "Дмитриев"));
        employees.add(new Employee("Максим", "Максимов"));
        employees.add(new Employee("Сергей", "Сергеев"));
        employees.add(new Employee("Андрей", "Андреев"));

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.execute(new CodeGenerator(employees));
        executor.execute(new ConsoleReader(employees));
        executor.execute(new CodeRequester(employees));
        executor.shutdown();
    }
}
