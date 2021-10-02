package net.badbird5907;

import static java.lang.System.getProperty;
import static java.lang.System.out;
import static oshi.util.FormatUtil.formatBytes;
import static oshi.util.FormatUtil.formatHertz;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.OperatingSystem;

public class Main {
	private static PrintStream ps;

    @SneakyThrows
    public static void main(String[] args) {
        String date = getDate();
        File logFile = new File(date + ".log");
        int i = 0;
        while (logFile.exists())
            logFile = new File(date + "_" + i++ + ".log");
        print("Creating log file (" + logFile + ")");
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ps = new PrintStream(logFile, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SystemInfo si = new SystemInfo();
        print(date);
        print("Java Version: " + getProperty("java.version"));
        print("ComputerInfo Version: " + ((Main.class.getPackage().getImplementationVersion() == null) ? "DEV" : Main.class.getPackage().getImplementationVersion()));
        print("Running EXE Version: " + (getProperty("launch4j") != null));
        print("Elevated: " + si.getOperatingSystem().isElevated());
        print("");
        HardwareAbstractionLayer hal = si.getHardware();
        print("----- Hardware Information -----");
        CentralProcessor cpu = hal.getProcessor();
        CentralProcessor.ProcessorIdentifier processorIdentifier = cpu.getProcessorIdentifier();
        print("CPU Info");
        print(" - Name: " + processorIdentifier.getName());
        print(" - Family: "  + processorIdentifier.getFamily());
        print(" - Model: "  + processorIdentifier.getModel());
        print(" - Vendor: "  + processorIdentifier.getVendor());
        print(" - Processor ID: "  + processorIdentifier.getProcessorID());
        print(" - Stepping: "  + processorIdentifier.getStepping());
        print(" - Identifier: " + processorIdentifier.toString());
        print(" - Microarchitecture: " + processorIdentifier.getMicroarchitecture());
        print(" - Max Frequency: " + formatHertz(cpu.getMaxFreq()));
        print(" - Vendor Freq: "  + formatHertz(processorIdentifier.getVendorFreq()));
        List<String> currentFreq = new ArrayList<>();
		for (long l : cpu.getCurrentFreq())
			currentFreq.add(formatHertz(l));
        print(" - Current Frequency: " + Arrays.toString(currentFreq.toArray(new String[0])));
        print(" - Physical Processors: " + cpu.getPhysicalProcessorCount());
        print(" - Logical Processors: " + cpu.getLogicalProcessorCount());
        print(" - Interrupts: " + cpu.getInterrupts());
        print(" - Uptime (Minutes): " + si.getOperatingSystem().getSystemUptime());
        print("");
        GlobalMemory ram = hal.getMemory();
        print("RAM Info");
        print(" - Total RAM: " + formatBytes(ram.getTotal()));
        print(" - Avaliable RAM: " + formatBytes(ram.getAvailable()));
        print("");
        print(" - Virtual RAM MAX: " + formatBytes(ram.getVirtualMemory().getVirtualMax()));
        print(" - Virtual RAM In Use: " + formatBytes(ram.getVirtualMemory().getVirtualInUse()));
        print(" - Virtual RAM Swap Total: " + formatBytes(ram.getVirtualMemory().getSwapTotal()));
        print("");
        print("Graphics Card(s)");
        List<GraphicsCard> graphicsCards = hal.getGraphicsCards();
        for (GraphicsCard graphicsCard : graphicsCards) {
            print(" - " + graphicsCard.getName());
            print("  - VRam: " + formatBytes(graphicsCard.getVRam()));
            print("  - Vendor: " + graphicsCard.getVendor());
            print("  - Version: " + graphicsCard.getVersionInfo());
            print("  - Device ID: " + graphicsCard.getDeviceId());
            print("");
        }
        print("Disks");
        for (HWDiskStore diskStore : hal.getDiskStores()) {
            print(" - " + diskStore.getName());
            print("  - Model: " + diskStore.getModel());
            print("  - Size: " + formatBytes(diskStore.getSize()));
            print("  - Reads: " + diskStore.getReads());
            print("  - Writes: " + diskStore.getWrites());
            print("  - Reads (bytes): " + formatBytes(diskStore.getReadBytes()));
            print("  - Writes (bytes): " + formatBytes(diskStore.getWriteBytes()));
            print("  - Partitions: " + diskStore.getPartitions().size());
            for (HWPartition partition : diskStore.getPartitions()) {
                print("   - " + partition.getName());
                print("    - Size: " + formatBytes(partition.getSize()));
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
                if ((powerSource.getVoltage() == -1) && (powerSource.getAmperage() == 0.0d) && !powerSource.isCharging() && !powerSource.isDischarging() && (powerSource.getCycleCount() == -1)) {
                    print(" - Unknown");
                    continue;
                }
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
            print("");
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
        Processes.printProcesses(si);
        ps.close();
    }

    public static void printUdpStats(InternetProtocolStats.UdpStats udpStats) {
        print("  - Datagrams Sent: " + udpStats.getDatagramsSent());
        print("  - Datagrams Received: " + udpStats.getDatagramsReceived());
        print("  - Datagrams Received-Errors: " + udpStats.getDatagramsReceivedErrors());
        print("  - Datagrams No Port: " + udpStats.getDatagramsNoPort());
    }

	public static void printTcpStats(InternetProtocolStats.TcpStats tcpStats) {
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

    public static String getDate() {
        return new SimpleDateFormat("MM-dd-yyy").format(new Date());
    }

	public static void print(String s) {
		out.println(s);
		if (ps != null)
			ps.println(s);
    }

	public static String humanReadableCPU(long a) {
		return mhzToGHz(a) + " GHz";
	}

	public static double mhzToGHz(double mhz) {
		return mhz / 1000000000;
	}

	public static void printUSBInfoTree(UsbDevice usbDevice, String pre) {
		print(pre + " - " + usbDevice.getName());
		print(pre + " - Serial Number: " + usbDevice.getSerialNumber());
		print(pre + " - Vendor: " + usbDevice.getVendor());
		print(pre + " - Device ID: " + usbDevice.getUniqueDeviceId());
		print(pre + " - Product ID: " + usbDevice.getProductId());
		print(pre + " - Vendor ID: " + usbDevice.getVendorId());
		if ((usbDevice.getConnectedDevices() != null) && !usbDevice.getConnectedDevices().isEmpty())
			for (UsbDevice connectedDevice : usbDevice.getConnectedDevices())
				printUSBInfoTree(connectedDevice, pre + " ");
	}

    //bytes -> kilobytes, megabytes, gigabyte, terabyte, petabyte, exabyte, zettabyte, yottabyte
	private static final long kilo = 1024, mega = kilo * kilo, giga = mega * kilo, tera = giga * kilo;
}
