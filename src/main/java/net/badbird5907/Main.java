package net.badbird5907;

import lombok.SneakyThrows;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {
    private static PrintStream ps;
    @SneakyThrows
    public static void main(String[] args) {
        String date = getDate();
        File logFile = new File(date + ".log");
        int i = 0;
        while (logFile.exists()){
            logFile = new File(date + "_" + i++ + ".log");
        }
        System.out.println("Creating log file (" + logFile + ")");
        logFile.createNewFile();
        ps = new PrintStream(logFile,"UTF-8");
        SystemInfo si = new SystemInfo();
        print(date);
        print("Java Version: " + System.getProperty("java.version"));
        print("ComputerInfo Version: " + (Main.class.getPackage().getImplementationVersion() == null ? "DEV" : Main.class.getPackage().getImplementationVersion()));
        print("Running EXE Version: " + (System.getProperty("launch4j") != null));
        print("Elevated: " + si.getOperatingSystem().isElevated());
        print("");
        HardwareAbstractionLayer hal = si.getHardware();
        print("----- Hardware Information -----");
        CentralProcessor cpu = hal.getProcessor();
        print("CPU Info");
        print(" - Max Frequency: " + humanReadableCPU(cpu.getMaxFreq()));
        List<String> currentFreq = new ArrayList<>();
        for (long l : cpu.getCurrentFreq()) currentFreq.add(humanReadableCPU(l));
        print(" - Current Frequency: " + Arrays.toString(currentFreq.toArray(new String[0])));
        print(" - Physical Processors: " + cpu.getPhysicalProcessorCount());
        print(" - Logical Processors: " + cpu.getLogicalProcessorCount());
        print(" - Interrupts: " + cpu.getInterrupts());
        print(" - Uptime: " + si.getOperatingSystem().getSystemUptime());
        print("");

        GlobalMemory ram = hal.getMemory();
        print("RAM Info");
        print(" - Total RAM: " + getSize(ram.getTotal()));
        print(" - Avaliable RAM: " + getSize(ram.getAvailable()));
        print("");
        print(" - Virtual RAM MAX: " + getSize(ram.getVirtualMemory().getVirtualMax()));
        print(" - Virtual RAM In Use: " + getSize(ram.getVirtualMemory().getVirtualInUse()));
        print(" - Virtual RAM Swap Total: " + getSize(ram.getVirtualMemory().getSwapTotal()));
        print("");
        print("Graphics Card(s)");
        List<GraphicsCard> graphicsCards = hal.getGraphicsCards();
        for (GraphicsCard graphicsCard : graphicsCards) {
            print(" - " + graphicsCard.getName());
            print("  - VRam: " + getSize(graphicsCard.getVRam()));
            print("  - Vendor: " + graphicsCard.getVendor());
            print("  - Version: " + graphicsCard.getVersionInfo());
            print("  - Device ID: " + graphicsCard.getDeviceId());
            print("");
        }
        print("Disks");
        for (HWDiskStore diskStore : hal.getDiskStores()) {
            print(" - " + diskStore.getName());
            print("  - Model: " + diskStore.getModel());
            print("  - Size: " + diskStore.getSize());
            print("  - Reads: " + diskStore.getReads());
            print("  - Writes: " + diskStore.getWrites());
            print("  - Reads (bytes): " + getSize(diskStore.getReadBytes()));
            print("  - Writes (bytes): " + getSize(diskStore.getWriteBytes()));
            print("  - Partitions: " + diskStore.getPartitions().size());
            for (HWPartition partition : diskStore.getPartitions()) {
                print("   - " + partition.getName());
                print("    - Size: " + partition.getSize());
                print("    - Type: " + partition.getType());
                print("    - ID: " + partition.getUuid());
                print("    - Major: " + partition.getMajor());
                print("    - Minor: " + partition.getMinor());
                print("    - Mount Point: " + partition.getMountPoint());
                print("    - Identification: " + partition.getIdentification());
                print("");
            }
            print("Power Sources");
            for (PowerSource powerSource : hal.getPowerSources()) {
                if (powerSource.getVoltage() == -1 && powerSource.getAmperage() == 0.0d && !powerSource.isCharging() && !powerSource.isDischarging() && powerSource.getCycleCount() == -1)
                    continue;
                print(" - " + powerSource.getName());
                print("  - Device Name: " + powerSource.getDeviceName());
                print("  - Manufacturer: " + powerSource.getManufacturer());
                print("  - Amperage: " + powerSource.getAmperage());
                print("  - Voltage: " + powerSource.getVoltage());
                print("  - Max Capacity: " + powerSource.getMaxCapacity());
                print("  - Discharging: " + powerSource.isDischarging());
                print("  - Charging: " + powerSource.isCharging());
                print("  - Cycle Count: " + powerSource.getCycleCount());
                print("  - Manufacture Date: " + powerSource.getManufactureDate());
                print("  - Temperate: " + powerSource.getTemperature());
                print("  - Current Capacity: " + powerSource.getCurrentCapacity());
                print("  - Serial Number: " + powerSource.getSerialNumber());
                print("  - Estimated Time Remaining: " + powerSource.getTimeRemainingEstimated());
                print("  - Remaining Percent: " + powerSource.getRemainingCapacityPercent());
                print("  - Chemistry: " + powerSource.getChemistry());
            }
            print("Sensors");
            Sensors sensors = hal.getSensors();
            print(" - CPU Temp: " + sensors.getCpuTemperature());
            print(" - CPU Voltage: " + sensors.getCpuVoltage());
            print(" - Fan Speeds: " + Arrays.toString(sensors.getFanSpeeds()));
        }
        print("");
        print("USB Devices");
        for (UsbDevice usbDevice : hal.getUsbDevices(true)) {
            printUSBInfoTree(usbDevice,"");
        }
        print("");
        print("Sound Cards");
        for (SoundCard soundCard : hal.getSoundCards()) {
            print(" - " + soundCard.getName());
            print("  - Codec: " + soundCard.getCodec());
            print("  - Driver Version: " + soundCard.getDriverVersion());
        }
        print("");
        ComputerSystem computerSystem = hal.getComputerSystem();
        print("Computer System");
        print(" - Hardware UUID: " + computerSystem.getHardwareUUID());
        print(" - Manufacturer: " + computerSystem.getManufacturer());
        print(" - Model: " + computerSystem.getModel());
        print(" - Serial Number: " + computerSystem.getSerialNumber());
        print(" - Base Board ");
        print("  - Model: " + computerSystem.getBaseboard().getModel());
        print("  - Manufacturer: " + computerSystem.getBaseboard().getManufacturer());
        print("  - Version: " + computerSystem.getBaseboard().getVersion());
        print("  - Serial Number: " + computerSystem.getBaseboard().getSerialNumber());
        print(" - Firmware");
        print("  - Name: " + computerSystem.getFirmware().getName());
        print("  - Manufacturer: " + computerSystem.getFirmware().getManufacturer());
        print("  - Version: " + computerSystem.getFirmware().getVersion());
        print("  - Description: " + computerSystem.getFirmware().getDescription());
        print("  - Release date: " + computerSystem.getFirmware().getReleaseDate());
        print("");
        print("----- OS Information -----");
        OperatingSystem os = si.getOperatingSystem();
        print(" - Family: " + os.getFamily());
        print(" - Version: " + os.getVersionInfo().getVersion());
        print(" - Build Number: " + os.getVersionInfo().getBuildNumber());
        print(" - Code Name: " + os.getVersionInfo().getCodeName());
        print(" - Manufacturer: " + os.getManufacturer());
        print(" - Uptime: " + os.getSystemUptime());
        print(" - Processes: " + os.getProcesses().size());
        print(" - Boot Time: " + new Date(os.getSystemBootTime()));
        print(" - Bitness: " + os.getBitness());
        print(" Internet Stats");
        InternetProtocolStats ips = os.getInternetProtocolStats();
        print("  - TCP v4");
        printTcpStats(ips.getTCPv4Stats());
        print("  - TCP v6");
        printTcpStats(ips.getTCPv6Stats());
        print("  - UDP v4");
        printUdpStats(ips.getUDPv4Stats());
        print("  - UDP v6");
        printUdpStats(ips.getUDPv6Stats());
        print("----- Process Information -----");
        
        ps.close();
    }
    public static void printUdpStats(InternetProtocolStats.UdpStats udpStats){
        print("  - Datagrams Sent: " + udpStats.getDatagramsSent());
        print("  - Datagrams Received: " + udpStats.getDatagramsReceived());
        print("  - Datagrams Received-Errors: " + udpStats.getDatagramsReceivedErrors());
        print("  - Datagrams No Port: " + udpStats.getDatagramsNoPort());
    }
    public static void printTcpStats(InternetProtocolStats.TcpStats tcpStats){
        print("   - Active Connections: " + tcpStats.getConnectionsActive());
        print("   - Established Connections: " + tcpStats.getConnectionsEstablished());
        print("   - Failed Connections: " + tcpStats.getConnectionFailures());
        print("   - Reset Connections: " + tcpStats.getConnectionsReset());
        print("   - Passive Connections: " + tcpStats.getConnectionsPassive());
        print("   - In Errors: " + tcpStats.getInErrors());
        print("   - Out Resets: " + tcpStats.getOutResets());
        print("   - Segments Received: " + tcpStats.getSegmentsReceived());
        print("   - Segments Re-Transmitted: " + tcpStats.getSegmentsRetransmitted());
        print("   - Segments Sent: " + tcpStats.getSegmentsSent());
    }
    public static String getDate(){
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyy");
        return df.format(new Date());
    }
    public static void print(String s){
        System.out.println(s);
        if (ps != null)
            ps.println(s);
    }
    public static String humanReadableCPU(long a){
        return mhzToGHz(a) + " GHz";
    }
    public static double mhzToGHz(double mhz){
        return mhz / 1000000000;
    }
    public static void printUSBInfoTree(UsbDevice usbDevice,String pre){
        print(pre + " - " + usbDevice.getName());
        print(pre + " - Serial Number: " + usbDevice.getSerialNumber());
        print(pre + " - Vendor: " + usbDevice.getVendor());
        print(pre + " - Device ID: " + usbDevice.getUniqueDeviceId());
        print(pre + " - Product ID: " + usbDevice.getProductId());
        print(pre + " - Vendor ID: " + usbDevice.getVendorId());
        if (usbDevice.getConnectedDevices() != null && !usbDevice.getConnectedDevices().isEmpty()){
            for (UsbDevice connectedDevice : usbDevice.getConnectedDevices()) {
                printUSBInfoTree(connectedDevice,pre + " ");
            }
        }
    }
    //bytes -> kilobytes, megabytes, gigabyte, terabyte, petabyte, exabyte, zettabyte, yottabyte
    private static final long kilo = 1024,mega = kilo * kilo,giga = mega * kilo, tera = giga * kilo;
    public static String getSize(long size) {
        String s = "";
        double kb = (double)size / kilo;
        double mb = kb / kilo;
        double gb = mb / kilo;
        double tb = gb / kilo;
        if(size < kilo) {
            s = size + " Bytes";
        } else if(size >= kilo && size < mega) {
            s =  String.format("%.2f", kb) + " KB";
        } else if(size >= mega && size < giga) {
            s = String.format("%.2f", mb) + " MB";
        } else if(size >= giga && size < tera) {
            s = String.format("%.2f", gb) + " GB";
        } else if(size >= tera) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }
}
