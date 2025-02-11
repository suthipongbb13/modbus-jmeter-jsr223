# Modbus TCP Client (JSR223 for JMeter)

## 📌 Overview
This Java program is designed to be used within a JSR223 Sampler in Apache JMeter. It connects to a Modbus TCP server, reads multiple holding registers, modifies the values, and writes them back. It uses the `jamod` library for Modbus communication.

## ⚙ Features
- Integrates with JMeter via JSR223 Sampler.
- Connects to a Modbus TCP server.
- Reads multiple holding registers using function code 03 (Read Multiple Registers).
- Updates specific register values and writes them back using function code 06 (Write Single Register).
- Implements randomized delays between read and write operations.
- Supports multiple iterations for continuous data exchange.
- Logs operations and register values for monitoring.

## 🚀 Prerequisites
- Apache JMeter installed.
- Java Development Kit (JDK) 8 or later.
- `jamod` library added to JMeter's `lib` folder.
- A running Modbus TCP server.

## 🛠 Installation & Setup in JMeter
1. Install Apache JMeter.
2. Download and add the `jamod` library (`jamod-1.2.jar`) to the `JMETER_HOME/lib` folder.
3. Open JMeter and create a Test Plan.
4. Add a `Thread Group` to your Test Plan.
5. Add a `JSR223 Sampler` inside the Thread Group.
6. Set `Script Language` to `groovy`.
7. Copy and paste the Java code into the script area.

## 🔧 Configuration
Edit the following parameters in the script:
```java
String modbusHost = "192.168.29.199";  // Change to your Modbus server IP
int modbusPort = 502;  // Default Modbus TCP port
int unitId = 1;  // Modbus slave ID
int ref = 0;  // Starting register address
int count = 100;  // Number of registers to read
int loopCount = 1000;  // Number of iterations
```

## 📌 Usage in JMeter
Run the JMeter test with the configured JSR223 Sampler. The script will:
- Establish a connection to the Modbus TCP server.
- Read register values.
- Modify and write back specific values.
- Log the execution details in the JMeter log.

## 📝 Example Output in JMeter Log
```
✅ Connected to Modbus Server at 192.168.29.199 with timeout 5000 ms
🔄 Iteration 1
📌 Register Values: R0=120 R1=130 R2=140 ...
⏳ Delaying before write for 150 ms
📝 Thread 5 updated R5 to 145
⏳ Delay for 120 ms
...
🔌 Connection closed successfully.
```

## 🛑 Error Handling
If an error occurs, it will be logged in the JMeter logs, and the connection will close safely. Example:
```
❌ Modbus Connection Error: Connection timeout
```

## 📚 References
- [Apache JMeter](https://jmeter.apache.org/)
- [Jamod Library](http://jamod.sourceforge.net/)
- [Modbus Protocol Specification](https://www.modbus.org/specs.php)

