import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class Process {
    int arrivalTime;
    int burstTime;
    int index;
    int remainingTime;

    Process(int arrivalTime, int burstTime, int index) {
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.index = index;
    }
}

public class FCFS_Scheduling {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();

        boolean validChoice = false;
        int choice = 0;
        while (!validChoice) {
            try {
                System.out.print("Choose a scheduling algorithm (1 for FCFS, 2 for SJF): ");
                choice = scanner.nextInt();

                if (choice == 1 || choice == 2) {
                    validChoice = true;
                } else {
                    System.out.println("Invalid choice. Please choose either 1 or 2.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a positive number (1 or 2).\n");
                scanner.next(); // Clear the invalid input
            }
        }

        int numProcesses = 0;
        while (true) {
            try {
                System.out.print("Enter the number of processes: ");
                numProcesses = scanner.nextInt();
                if (numProcesses > 0) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a positive number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a positive number.");
                scanner.next(); // Clear the invalid input
            }
        }

        for (int i = 0; i < numProcesses; i++) {
            int arrivalTime = -1;
            int burstTime = -1;
            while (arrivalTime < 0) {
                try {
                    System.out.print("Enter arrival time for process " + (i + 1) + ": ");
                    arrivalTime = scanner.nextInt();
                    if (arrivalTime < 0) {
                        System.out.println("Invalid input. Please enter a non-negative number.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a non-negative number.");
                    scanner.next(); // Clear the invalid input
                }
            }

            while (burstTime <= 0) {
                try {
                    System.out.print("Enter burst time for process " + (i + 1) + ": ");
                    burstTime = scanner.nextInt();
                    if (burstTime <= 0) {
                        System.out.println("Invalid input. Please enter a positive number.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a positive number.");
                    scanner.next(); // Clear the invalid input
                }
            }

            processes.add(new Process(arrivalTime, burstTime, i + 1));
        }

        if (choice == 1) {
            fcfsScheduling(processes);
        } else if (choice == 2) {
            System.out.print("Choose SJF mode (1 for Non-Preemptive, 2 for Preemptive): ");
            int sjfChoice = scanner.nextInt();
            if (sjfChoice == 1) {
                sjfScheduling(processes);
            } else if (sjfChoice == 2) {
                preemptiveSjfScheduling(processes);
            } else {
                System.out.println("Invalid choice. Defaulting to Non-Preemptive SJF.");
                sjfScheduling(processes);
            }
        }

        scanner.close();
    }

    public static void fcfsScheduling(List<Process> processes) {
        int n = processes.size();
        int[] waitTime = new int[n];
        int[] turnaroundTime = new int[n];
        int totalWaitTime = 0;
        int totalTurnaroundTime = 0;

        // Sort processes by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        // Calculate waiting time for each process
        for (int i = 1; i < n; i++) {
            waitTime[i] = processes.get(i - 1).burstTime + waitTime[i - 1] - processes.get(i).arrivalTime + processes.get(i - 1).arrivalTime;
            if (waitTime[i] < 0) {
                waitTime[i] = 0;
            }
        }

        // Calculate turnaround time for each process
        for (int i = 0; i < n; i++) {
            turnaroundTime[i] = processes.get(i).burstTime + waitTime[i];
        }

        // Calculate total waiting time and total turnaround time
        for (int i = 0; i < n; i++) {
            totalWaitTime += waitTime[i];
            totalTurnaroundTime += turnaroundTime[i];
        }

        // Calculate average waiting time and average turnaround time
        double avgWaitTime = (double) totalWaitTime / n;
        double avgTurnaroundTime = (double) totalTurnaroundTime / n;

        // Display process details
        System.out.println(String.format("\n%-10s %-15s %-15s %-15s %-15s", "Process", "Arrival Time", "Burst Time", "Waiting Time", "Turnaround Time"));
        for (int i = 0; i < n; i++) {
            System.out.printf("%-10d %-15d %-15d %-15d %-15d\n", processes.get(i).index, processes.get(i).arrivalTime, processes.get(i).burstTime, waitTime[i], turnaroundTime[i]);
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWaitTime);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTurnaroundTime);

        // Display process representation
        System.out.println("\nProcess Representation:");
        int ganttTime = 0;
        for (Process p : processes) {
            if (ganttTime < p.arrivalTime) {
                ganttTime = p.arrivalTime;
            }
            System.out.print(ganttTime + "[P" + p.index + "]" + (ganttTime + p.burstTime));
            ganttTime += p.burstTime;
            if (processes.indexOf(p) < n - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    public static void sjfScheduling(List<Process> processes) {
        int n = processes.size();
        int[] waitTime = new int[n];
        int[] turnaroundTime = new int[n];
        int totalWaitTime = 0;
        int totalTurnaroundTime = 0;
        List<Process> completedProcesses = new ArrayList<>();

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;

        while (completedProcesses.size() < n) {
            List<Process> availableProcesses = new ArrayList<>();
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !completedProcesses.contains(p)) {
                    availableProcesses.add(p);
                }
            }

            if (availableProcesses.isEmpty()) {
                currentTime++;
                continue;
            }

            availableProcesses.sort(Comparator.comparingInt(p -> p.burstTime));
            Process currentProcess = availableProcesses.get(0);
            completedProcesses.add(currentProcess);

            waitTime[currentProcess.index - 1] = currentTime - currentProcess.arrivalTime;
            currentTime += currentProcess.burstTime;
            turnaroundTime[currentProcess.index - 1] = waitTime[currentProcess.index - 1] + currentProcess.burstTime;
        }

        // Calculate total waiting time and total turnaround time
        for (int i = 0; i < n; i++) {
            totalWaitTime += waitTime[i];
            totalTurnaroundTime += turnaroundTime[i];
        }

        // Calculate average waiting time and average turnaround time
        double avgWaitTime = (double) totalWaitTime / n;
        double avgTurnaroundTime = (double) totalTurnaroundTime / n;

        // Display process details
        System.out.println(String.format("\n%-10s %-15s %-15s %-15s %-15s", "Process", "Arrival Time", "Burst Time", "Waiting Time", "Turnaround Time"));
        for (Process p : processes) {
            System.out.printf("%-10d %-15d %-15d %-15d %-15d\n", p.index, p.arrivalTime, p.burstTime, waitTime[p.index - 1], turnaroundTime[p.index - 1]);
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWaitTime);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTurnaroundTime);

        // Display process representation in Gantt chart format based on completion time
        System.out.println("\nProcess Representation:");
        int ganttTime = 0;
        for (Process p : completedProcesses) {
            if (ganttTime < p.arrivalTime) {
                ganttTime = p.arrivalTime;
            }
            System.out.print(ganttTime + "[P" + p.index + "]" + (ganttTime + p.burstTime));
            ganttTime += p.burstTime;
            if (completedProcesses.indexOf(p) < n - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    public static void preemptiveSjfScheduling(List<Process> processes) {
        int n = processes.size();
        int[] waitTime = new int[n];
        int[] turnaroundTime = new int[n];
        int totalWaitTime = 0;
        int totalTurnaroundTime = 0;
        int currentTime = 0;
        int completed = 0;
        List<String> ganttChart = new ArrayList<>();

        while (completed < n) {
            Process shortestJob = null;
            int minRemainingTime = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingTime > 0 && p.remainingTime < minRemainingTime) {
                    shortestJob = p;
                    minRemainingTime = p.remainingTime;
                }
            }

            if (shortestJob == null) {
                currentTime++;
                continue;
            }

            shortestJob.remainingTime--;
            ganttChart.add("P" + shortestJob.index);
            currentTime++;

            if (shortestJob.remainingTime == 0) {
                completed++;
                int finishTime = currentTime;
                waitTime[shortestJob.index - 1] = finishTime - shortestJob.arrivalTime - shortestJob.burstTime;
                turnaroundTime[shortestJob.index - 1] = finishTime - shortestJob.arrivalTime;
            }
        }

        // Calculate total waiting time and total turnaround time
        for (int i = 0; i < n; i++) {
            totalWaitTime += waitTime[i];
            totalTurnaroundTime += turnaroundTime[i];
        }

        // Calculate average waiting time and average turnaround time
        double avgWaitTime = (double) totalWaitTime / n;
        double avgTurnaroundTime = (double) totalTurnaroundTime / n;

        // Display process details
        System.out.println(String.format("\n%-10s %-15s %-15s %-15s %-15s", "Process", "Arrival Time", "Burst Time", "Waiting Time", "Turnaround Time"));
        for (Process p : processes) {
            System.out.printf("%-10d %-15d %-15d %-15d %-15d\n", p.index, p.arrivalTime, p.burstTime, waitTime[p.index - 1], turnaroundTime[p.index - 1]);
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWaitTime);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTurnaroundTime);

        // Display Gantt chart representation
        System.out.println("\nGantt Chart Representation:");
        String prevProcess = "";
        int ganttStartTime = 0;
        for (int i = 0; i < ganttChart.size(); i++) {
            String currentProcess = ganttChart.get(i);
            if (!currentProcess.equals(prevProcess)) {
                if (!prevProcess.isEmpty()) {
                    System.out.print(ganttStartTime + "[" + prevProcess + "]" + i);
                    ganttStartTime = i;
                    if (i < ganttChart.size()) {
                        System.out.print(" -> ");
                    }
                }
                prevProcess = currentProcess;
            }
        }
        System.out.print(ganttStartTime + "[" + prevProcess + "]" + ganttChart.size());
        System.out.println();
    }
}
