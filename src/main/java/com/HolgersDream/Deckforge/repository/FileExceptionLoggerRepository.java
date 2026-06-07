package com.HolgersDream.Deckforge.repository;

import com.HolgersDream.Deckforge.domain.interfaces.IExceptionLoggerRepository;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Repository
public class FileExceptionLoggerRepository implements IExceptionLoggerRepository {
    private String filePath;
    private String backupFilePath;
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");


    @Override
    public void saveExceptionMessage(Exception e, String details){
        setFilePath();
        // Prøver at oprette eller skrive til en fil i logs mappen.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))){
            writer.write("\n" + LocalTime.now().format(timeFmt) + " : " + e.getMessage()
            + "\n " + details);
        } catch (IOException ioe) {
            setBackupFilePath();
            // I tilfælde af fejl ved første lokation.
            // Prøver at skrive til et lokalt sted på devicen, samt dokumenterer fejlen, der skete ovenover.
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFilePath, true))){
                writer.write("\n" + LocalTime.now().format(timeFmt) + " : " + e.getMessage() +
                        "\n " + details +
                        "\n Indlæsningsfejl i forhold til fil : " + ioe.getMessage());
            } catch (IOException ioe2) {
                //Ingen idé hvis backup fejler
            }
        }
    }

    private void setFilePath(){
        this.filePath = "logs\\" + getLogName();
    }

    private void setBackupFilePath(){
        // finder stien til den lokale bruger-mappe home-directory
        this.backupFilePath = Paths.get(System.getProperty("user.home")) + "\\" + getLogName();
    }


    private String getLogName(){
        return LocalDate.now() + "-log.txt";
    }

}


