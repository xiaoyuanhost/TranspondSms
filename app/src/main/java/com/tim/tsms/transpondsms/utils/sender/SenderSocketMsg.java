//pull request test
package com.tim.tsms.transpondsms.utils.sender;

import static com.tim.tsms.transpondsms.SenderActivity.NOTIFY;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.tim.tsms.transpondsms.utils.SettingUtil; //新增,修改了SettingUtil中的内容
import com.tim.tsms.transpondsms.utils.sender.SendHistory;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;

/**
 * SenderSocketMsg 类负责通过 TCP 协议发送消息到指定的 IP 地址和端口。
 * 支持同步和异步的消息发送功能。
 */
public class SenderSocketMsg {

    // 日志标签，用于标识日志输出来源
    static String TAG = "SenderSocketMsg";

    /**
     * 直接发送消息的方法。
     * 该方法是同步的，执行过程中会阻塞当前线程，直到消息发送完成或发生异常。
     *
     * @param msg       要发送的消息内容
     * @throws Exception 如果发送失败或未正确配置 IP 地址和端口，则抛出异常
     */
    public static void sendMsg(String msg) throws Exception {

        String ipAddress = SettingUtil.get_using_socket_ipAddress();
        String port = SettingUtil.get_socket_port();
        if (ipAddress.equals("") || port.equals("")) {
            return;
        }
        final int portNumber;
        portNumber = Integer.parseInt(port);
        // 检查 IP 地址是否有效
        /*
        if (ipAddress == null || ipAddress.isEmpty()) {
            return;  // 如果 IP 地址为空，则直接返回
        }*/
        /*
        if (portNumber == -1) {
            return;
        }
        */

        // 构建消息体
        String textMsg = "{ \"title\": \"通知\", \"content\": \"" + msg + "\" }";
        Log.i(TAG, "sendMsg ipAddress:" + ipAddress + " port:" + portNumber + " msg:" + msg);
        final String msgf = msg;  // 保存消息内容，供异常记录使用

        Socket socket = null;
        BufferedWriter writer = null;

        try {
            // 创建 TCP 连接到指定的 IP 地址和端口
            socket = new Socket(ipAddress, portNumber);

            // 创建 BufferedWriter 用于向服务器发送数据
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            // 发送消息并换行
            writer.write(msg);
            writer.newLine();
            writer.flush();
            Log.i(TAG, "消息发送成功");

        } catch (IOException e) {
            // 捕获异常并记录失败信息
            Log.d(TAG, "onFailure：" + e.getMessage());
            SendHistory.addHistory("Socket转发:" + msgf + " onFailure：" + e.getMessage());

        } finally {
            // 关闭资源
            if (writer != null) {
                try {
                    writer.close();  // 关闭 BufferedWriter
                } catch (IOException e) {
                    Log.e(TAG, "关闭输出流失败：" + e.getMessage());
                }
            }
            if (socket != null) {
                try {
                    socket.close();  // 关闭 Socket 连接
                } catch (IOException e) {
                    Log.e(TAG, "关闭 socket 失败：" + e.getMessage());
                }
            }
        }
    }

    /**
     * 使用 Handler 发送消息的方法，支持异步回调。
     * 该方法在新线程中发送消息，不会阻塞主线程。
     * 通过 handError 处理发送结果的回调。
     *
     * @param handError 处理错误信息的 Handler，用于异步反馈发送状态
     * @param ipAddress 目标服务器的 IP 地址
     * @param port      目标服务器的端口
     * @param msg       要发送的消息内容
     * @throws Exception 如果发送失败或未正确配置 IP 地址和端口，则抛出异常
     */
    public static void sendMsg(final Handler handError, final String ipAddress, final String port, final String msg) throws Exception {
        // 检查 IP 地址是否有效

        if (ipAddress == null || ipAddress.isEmpty()||port == null || port.isEmpty()) {
            return;  // 如果 IP 地址为空，则直接返回
        }
      
        final int portNumber;
        portNumber = Integer.parseInt(port);
        Log.i(TAG, "sendMsg ipAddress:" + ipAddress + " port:" + portNumber + " msg:" + msg);
        final String msgf = msg;  // 保存消息内容，供异常记录使用

        // 启动新线程进行异步发送
        new Thread(new Runnable() {
            Socket socket = null;
            BufferedWriter writer = null;

            @Override
            public void run() {
                try {
                    // 创建 TCP 连接到指定的 IP 地址和端口
                    socket = new Socket(ipAddress, portNumber);

                    // 创建 BufferedWriter 用于向服务器发送数据
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

                    // 发送消息并换行
                    writer.write(msg);
                    writer.newLine();
                    writer.flush();
                    Log.i(TAG, "消息发送成功");

                    // 发送成功后的回调
                    if (handError != null) {
                        android.os.Message message = new android.os.Message();
                        message.what = NOTIFY;
                        Bundle bundle = new Bundle();
                        bundle.putString("DATA", "消息发送成功");
                        message.setData(bundle);
                        handError.sendMessage(message);  // 通知主线程发送成功
                    }

                } catch (IOException e) {
                    // 捕获异常并记录失败信息
                    Log.d(TAG, "onFailure：" + e.getMessage());

                    // 发送失败后的回调
                    if (handError != null) {
                        android.os.Message message = new android.os.Message();
                        message.what = NOTIFY;
                        Bundle bundle = new Bundle();
                        bundle.putString("DATA", "发送失败：" + e.getMessage());
                        message.setData(bundle);
                        handError.sendMessage(message);  // 通知主线程发送失败
                    }

                } finally {
                    // 关闭资源
                    try {
                        if (writer != null) writer.close();  // 关闭 BufferedWriter
                        if (socket != null) socket.close();  // 关闭 Socket 连接
                    } catch (IOException e) {
                        Log.e(TAG, "关闭资源失败：" + e.getMessage());
                    }
                }
            }
        }).start();  // 启动线程
    }
}
