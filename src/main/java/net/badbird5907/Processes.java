package net.badbird5907;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;

import static net.badbird5907.Main.print;

public class Processes {
    public static void printProcesses(SystemInfo systemInfo){
        print("-----------------------------------------------------------------------------");
        print(String.format("%10s %30s %20s %5s %5s", "PID", "CPU %", "Process Name", "%MEM", "Threads"));
        print("-----------------------------------------------------------------------------");
       /*
        for(Student student: students){
            System.out.format("%10s %30s %20s %5d %5c",
                    student.getId(), student.getEmailId(), student.getName(), student.getAge(), student.getGrade());
            print();
        }
        */
        for (OSProcess process : systemInfo.getOperatingSystem().getProcesses()) {
            print(String.format("%10s %30s %20s %5d %5d", process.getProcessID(), toPercentage(process.getProcessCpuLoadCumulative()), process.getName(), process.getResidentSetSize() / systemInfo.getHardware().getMemory().getTotal(), process.getThreadCount(),process.getProcessID(), process.getProcessCpuLoadCumulative(), process.getName(), process.getResidentSetSize() / systemInfo.getHardware().getMemory().getTotal(), process.getThreadCount()));
            print("");
        }
        print("-----------------------------------------------------------------------------");
    }
    public static String toPercentage(double n){
        return String.format("%.0f",n * 100)+"%";
    }
}