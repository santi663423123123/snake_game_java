/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.game;

/**
 *
 * @author Usuario
 */
public class CollisionInfo {
    private String clientPlayerName;
    private String roomName;
    private int secondsPlayed;
    private int fruitCount;

    public CollisionInfo(String clientPlayerName, String roomName, int secondsPlayed, int fruitCount) {
        this.clientPlayerName = clientPlayerName;
        this.roomName = roomName;
        this.secondsPlayed = secondsPlayed;
        this.fruitCount = fruitCount;
    }

    public String getClientPlayerName() {
        return clientPlayerName;
    }

    public void setClientPlayerName(String clientPlayerName) {
        this.clientPlayerName = clientPlayerName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getSecondsPlayed() {
        return secondsPlayed;
    }

    public void setSecondsPlayed(int secondsPlayed) {
        this.secondsPlayed = secondsPlayed;
    }

    public int getFruitCount() {
        return fruitCount;
    }

    public void setFruitCount(int fruitCount) {
        this.fruitCount = fruitCount;
    }

    @Override
    public String toString() {
        return String.format("Client: %s, Room: %s, Time Played: %d, Fruits: %d",
                             clientPlayerName, roomName, secondsPlayed, fruitCount);
    }
}
