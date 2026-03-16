package edu.kit.informatik.ui;

import edu.kit.informatik.logic.LogicException;
import edu.kit.informatik.logic.SimEngine;
import edu.kit.informatik.logic.entitys.Car;
import edu.kit.informatik.logic.entitys.Crossing;
import edu.kit.informatik.logic.entitys.CrossingWithLight;
import edu.kit.informatik.logic.entitys.Entity;
import edu.kit.informatik.logic.entitys.Street;
import edu.kit.informatik.logic.entitys.StreetWithFastLane;
import edu.kit.kastel.trafficsimulation.io.SimulationFileLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/** Diese Klasse macht die Benutzerinteraktion des Programms.
 * Sie liest die Eingaben ein und gibt die Ausgaben aus.
 * @author ujiqk
 * @version 1.0
 */
public class SimUi {

    private static final String READY = "READY";
    private static final String OUTPUT = "Car %d on street %d with speed %d and position %d";
    private static final String ERROR_INVALID_TICK_NUMBER = "Error: Invalid Number of Ticks!";
    private static final String ERROR_DATA_NOT_LOADED = "Error: Data not loaded!";
    private static final String ERROR_INVALID_ID = "Error: Invalid Id!";
    private static final String ERROR_INVALID_FOLDER_PATH = "Error: Invalid Folder Path!";
    private static final String ERROR_INVALID_COMMAND = "Error: Invalid Command!";
    private static final String ERROR_DEFAULT = "Error: ";

    private boolean quit;
    private boolean fileLoaded;
    private SimEngine simEngine;
    private int lastStreetId;       //nur zum Netzwerke laden

    /** Erstellt eine standard Benutzerwinteraktionsklasse.
     * Sie liest die Eingaben ein und gibt die Ausgaben aus.
     */
    public SimUi() {
        lastStreetId = -1;
        fileLoaded = false;
        Scanner inputScanner = new Scanner(System.in);
        quit = false;
        while (!quit) {
            readInput(inputScanner.nextLine());
        }
        inputScanner.close();
    }

    private void readInput(String input) {
        //'quit' befehl befolgen
        if (Pattern.matches("quit", input)) {
            quit = true;
            //Hiernach darf nichts mehr kommen
        }
        //'load' Befehl
        else if (Pattern.matches("load \\S+", input)) {       //Regex für Dateien
            loadFile(input.split(" ")[1]);
        }
        //'simulate' Befehl
        else if (Pattern.matches("simulate \\d+", input)) {
            simulate(input.split(" ")[1]);
        }
        //'position' Befehl
        else if (Pattern.matches("position \\d+", input)) {
            printPosition(input.split(" ")[1]);
        }
        //kein gültiger Befehl
        else {
            System.err.println(ERROR_INVALID_COMMAND);
        }
    }

    private void loadFile(String path) {
        List<String> cars;
        List<String> streets;
        List<String> crossings;
        lastStreetId = -1;
        //Dateien laden
        try {
            SimulationFileLoader fileLoader = new SimulationFileLoader(path);
            cars = fileLoader.loadCars();
            streets = fileLoader.loadStreets();
            crossings = fileLoader.loadCrossings();
        }
        catch (IOException e) {
            System.err.println(ERROR_INVALID_FOLDER_PATH);
            return;
        }
        //Versuchen ein Straßennetzwerk zu erzeugen
        try {
            simEngine = new SimEngine(parseCars(cars), parseStreets(streets), parseCrossings(crossings));
        }
        catch (LogicException | IllegalArgumentException e) {
            System.err.println(ERROR_DEFAULT + e.getMessage());
            return;
        }
        fileLoaded = true;
        System.out.println(READY);
    }

    private List<Entity> parseCars(List<String> carsStrings) throws IllegalArgumentException {
        //aus Strings Autos bekommen
        List<Entity> cars = new ArrayList<>();
        for (String entry : carsStrings) {
            if (Pattern.matches("\\d+,\\d+,\\d+,\\d+", entry)) {
                //einzelne Zahlen parsen
                int id = Integer.parseInt(entry.split(",")[0]);
                int streetId = Integer.parseInt(entry.split(",")[1]);
                int desiredVelocity = Integer.parseInt(entry.split(",")[2]);
                int acceleration = Integer.parseInt(entry.split(",")[3]);
                Car next = new Car(id, desiredVelocity, acceleration, streetId);
                cars.add(next);         //kann IllegalArgument werfen
            }
            else {
                throw new IllegalArgumentException("wrong car file format");
            }
        }
        return cars;
    }

    private List<Entity> parseCrossings(List<String> crossingsStrings) throws IllegalArgumentException {
        //aus Strings Kreuzungen bekommen
        List<Entity> crossings = new ArrayList<>();
        for (String entry : crossingsStrings) {
            if (Pattern.matches("\\d+:\\d+t", entry)) {
                //Einzelne Zahlen parsen
                int id = Integer.parseInt(entry.split(":")[0]);
                int duration = Integer.parseInt(entry.split(":")[1].split("t")[0]);
                Crossing next;
                if (duration == 0) {
                    next = new Crossing(id);                        //wirft IllegalArgument
                }
                else {
                    next = new CrossingWithLight(id, duration);     //wirft IllegalArgument
                }
                crossings.add(next);
            }
            else {
                throw new IllegalArgumentException("wrong crossing file format");
            }
        }
        return crossings;
    }

    private List<Entity> parseStreets(List<String> streetsStrings) throws IllegalArgumentException {
        //aus Strings Straßen bekommen
        List<Entity> streets = new ArrayList<>();
        for (String entry : streetsStrings) {
            if (Pattern.matches("\\d+-->\\d+:\\d+m,[1,2]x,\\d+max", entry)) {
                //einzelne Zahlen parsen
                int startNode = Integer.parseInt(entry.split("-")[0]);
                int endNode = Integer.parseInt(entry.split(">")[1].split(":")[0]);
                int length = Integer.parseInt(entry.split(":")[1].split("m")[0]);
                int type = Integer.parseInt(entry.split(",")[1].split("x")[0]);
                int maxVelocity = Integer.parseInt(entry.split(",")[2].split("max")[0]);
                int id = getNewStreetID();
                Street next;
                if (type == 1) {        //normale Straße
                    next = new Street(id, length, maxVelocity, startNode, endNode);             //wirft IllegalArgument
                }
                else if (type == 2) {   //mit Überholspur
                    next = new StreetWithFastLane(id, length, maxVelocity, startNode, endNode); //wirft IllegalArgument
                }
                else {
                    throw new IllegalArgumentException("illegal street type");       //falscher Typ
                }
                streets.add(next);
            }
            else {
                throw new IllegalArgumentException("wrong Street file format");
            }
        }
        return streets;
    }

    private int getNewStreetID() {
        int id = lastStreetId + 1;
        lastStreetId = id;
        return id;
    }

    private void simulate(String ticksString) {
        if (!fileLoaded ) {             //Noch kein Netzwerk geladen
            System.err.println(ERROR_DATA_NOT_LOADED);
            return;
        }
        int ticks;
        try {
            ticks = Integer.parseInt(ticksString);
        }
        catch (NumberFormatException e) {
            System.err.println(ERROR_INVALID_TICK_NUMBER);
            return;
        }
        if (ticks < 0) {
            System.err.println(ERROR_INVALID_TICK_NUMBER);
            return;
        }
        for (int i = 0; i < ticks; i++) {
            simEngine.doOneTick();
        }
        System.out.println(READY);
    }

    private void printPosition(String idString) {
        if (!fileLoaded ) {
            System.err.println(ERROR_DATA_NOT_LOADED);      //Noch kein Netzwerk geladen
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idString);
        }
        catch (NumberFormatException e) {
            System.err.println(ERROR_INVALID_ID);
            return;
        }
        Car car = simEngine.getCar(id);
        if (car == null) {
            System.err.println(ERROR_INVALID_ID);       //Auto gibt es nicht
        }
        else {
            try {
                System.out.printf(OUTPUT, id, car.getStreetID(), car.getVelocity(), simEngine.getCarLocation(id));
                System.out.println();
            }
            catch (LogicException e) {
                System.err.println(ERROR_DEFAULT + e.getMessage());     //Auto gibt es nicht 2
            }
        }

    }

}
