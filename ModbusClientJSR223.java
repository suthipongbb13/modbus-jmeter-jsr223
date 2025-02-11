import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.procimg.SimpleRegister;
import java.net.InetAddress;
import java.util.Random;

// ✅ ตั้งค่า Modbus Server
//String modbusHost = "127.0.0.1";  // 🔹 เปลี่ยนเป็น IP ของ Modbus Server
//String modbusHost = "61.91.5.188";  // 🔹 เปลี่ยนเป็น IP ของ Modbus Server
String modbusHost = "192.168.29.199";  // 🔹 เปลี่ยนเป็น IP ของ Modbus Server
int modbusPort = 502;  // 🔹 Default Port ของ Modbus TCP
int unitId = 1; // 🔹 กำหนด Modbus ID
int ref = 0;  // 🔹 รีจิสเตอร์เริ่มต้นที่ต้องการอ่าน
int count = 100; // 🔹 จำนวนรีจิสเตอร์ที่ต้องการอ่าน
int loopCount = 1000;  // จำนวนรอบที่ต้องการให้ทำงาน

// ✅ สร้างการเชื่อมต่อ Modbus
TCPMasterConnection conn = null;
Random random = new Random();
try {
    InetAddress addr = InetAddress.getByName(modbusHost);
    conn = new TCPMasterConnection(addr);
    conn.setPort(modbusPort);
    conn.connect();  // ✅ เชื่อมต่อกับ Modbus Server
    
    conn.setTimeout(5000);  // ✅ ตั้ง Timeout 5 วินาที
    log.info("✅ Connected to Modbus Server at " + modbusHost + " with timeout 5000 ms");

    for (int i = 0; i < loopCount; i++) {
        log.info("🔄 Iteration " + (i + 1));

        // ✅ ส่งคำขออ่านค่า Holding Registers (FC03) พร้อมกำหนด Modbus ID
        ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(ref, count);
        req.setUnitID(unitId);
        ModbusTCPTransaction trans = new ModbusTCPTransaction(conn);
        trans.setRequest(req);
        trans.execute();

        // ✅ รับค่าจากเซิร์ฟเวอร์
        ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();

        int threadNum = ctx.getThreadNum();
        if (threadNum < count) {
            int regValue = res.getRegister(threadNum).toShort();
            int newValue = regValue + 1;

            int writeDelay = 100 + random.nextInt(101);
            log.info("⏳ Delaying before write for " + writeDelay + " ms");
            Thread.sleep(writeDelay);

            // ✅ เขียนค่ากลับไปยังรีจิสเตอร์ พร้อมกำหนด Modbus ID
            WriteSingleRegisterRequest writeReq = new WriteSingleRegisterRequest(ref + threadNum, new SimpleRegister(newValue));
            writeReq.setUnitID(unitId);
            ModbusTCPTransaction writeTrans = new ModbusTCPTransaction(conn);
            writeTrans.setRequest(writeReq);
            writeTrans.execute();
            log.info("📝 Thread " + threadNum + " updated R" + (ref + threadNum) + " to " + newValue);
        }

        StringBuilder values = new StringBuilder("📌 Register Values: ");
        for (int j = 0; j < count; j++) {
            values.append("R" + (ref + j) + "=" + res.getRegister(j).toShort() + " ");
        }
        log.info(values.toString());
        vars.put("modbus_values", values.toString());
        SampleResult.setResponseData(values.toString(), "UTF-8");

        int delayTime = 100 + random.nextInt(101);
        log.info("⏳ Delay for " + delayTime + " ms");
        Thread.sleep(delayTime);
    }        
} catch (Exception e) {
    log.error("❌ Modbus Connection Error: " + e.getMessage());
    SampleResult.setResponseData("❌ Error: " + e.getMessage(), "UTF-8");
    SampleResult.setSuccessful(false);
} finally {
    if (conn != null) {
        try {
            if (conn.getModbusTransport() != null) {
                conn.getModbusTransport().close();
            }
            conn.close();
            log.info("🔌 Connection closed successfully.");
        } catch (Exception ex) {
            log.error("⚠ Error closing connection: " + ex.getMessage(), ex);
        }
    }
}
