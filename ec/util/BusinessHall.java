package com.evangelsoft.econnect.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Vector;

public class BusinessHall<E> {
    private String hallName; // 大厅名称
    private int seatCount; // 座位数
    private int maxQueueSize; // 最大排队长度
    private Vector<Object> seats = new Vector<>(); // 座位列表
    private Vector<E> queue = new Vector<>(); // 排队列表
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BusinessHall.class.getPackage().getName() + ".Res");

    //接收大厅名称、座位数和最大排队长度，初始化大厅。
    public BusinessHall(String hallName, int seatCount, int maxQueueSize) {
        this.hallName = hallName;
        if (seatCount <= 0) {
            seatCount = -1;
        }

        if (maxQueueSize <= 0) {
            maxQueueSize = -1;
        }

        this.seatCount = seatCount;
        this.maxQueueSize = maxQueueSize;
    }
    
//顾客到达大厅。如果有空座位，顾客将占据一个座位；如果没有空座位但排队未满，顾客将加入队列。
    public synchronized boolean arrive(E e) throws Exception {
        return this.arrive(e, false);
    }
//顾客到达大厅。如果有空座位，顾客将占据一个座位；如果没有空座位但排队未满，顾客将加入队列。
    public synchronized boolean arrive(E e, boolean forceQueue) throws Exception {
        if (this.seatCount > 0 && this.seats.size() >= this.seatCount && !forceQueue) {
            if (this.maxQueueSize > 0 && this.queue.size() >= this.maxQueueSize) {
                throw new Exception(MessageFormat.format(resourceBundle.getString("MSG_BUSINESS_HALL_IS_OVERLOADED"), this.hallName));
            } else {
                this.queue.add(e);
                return false;
            }
        } else {
            this.seats.add(e);
            return true;
        }
    }
//从排队中移动下一个顾客到座位上。此方法可根据条件判断是否将顾客从队列移到座位。
    public synchronized Object next(boolean forceQueue) {
        if (this.queue.size() != 0 && (!forceQueue || this.seatCount <= 0 || this.seats.size() < this.seatCount)) {
            Object obj = this.queue.remove(0);
            this.seats.add(obj);
            return obj;
        } else {
            return null;
        }
    }
//顾客离开座位。
    public synchronized boolean leave(E e) {
        return this.seats.remove(e);
    }
//顾客离开座位。
    public synchronized boolean giveUp(E e) {
        return this.queue.remove(e);
    }
//清空座位和排队列表。
    public synchronized void clear() {
        this.seats.clear();
        this.queue.clear();
    }
 //   仅清空排队列表。
    public synchronized void clearQueue() {
        this.queue.clear();
    }
//提供座位和排队的统计信息。
    public int getSeatCount() {
        return this.seatCount;
    }
  //提供座位和排队的统计信息。
    public int getOccupiedCount() {
        return this.seats.size();
    }
  //提供座位和排队的统计信息。
    public int getEmptyCount() {
        return this.seatCount > 0 ? this.seatCount - this.seats.size() : 2147483647;
    }
  //提供座位和排队的统计信息。
    public int getQueuerCount() {
        return this.queue.size();
    }
//获取座位列表。
    public Vector<Object> getSeats() {
        return this.seats;
    }
//获取排队列表。
    public Vector<E> getQueue() {
        return this.queue;
    }
}