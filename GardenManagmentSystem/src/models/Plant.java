package models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;

public class Plant implements DBConnectionable{

    private int plantId, gardenId, waterPeriod;
    private String plantName, description;
    private LocalDate plantingDate, lastWaterDate, nextWaterDate;
    private File imageFile;

    /**
     * Creates plant object with default image
     * @param plantName
     * @param description
     * @param plantingDate
     * @param lastWaterDate
     * @param waterPeriod
     * @param gardenId
     */
    public Plant(String plantName, String description, LocalDate plantingDate, LocalDate lastWaterDate, int waterPeriod, int gardenId){
        setPlantName(plantName);
        setDescription(description);
        setPlantingDate(plantingDate);
        setLastWaterDate(lastWaterDate);
        setWaterPeriod(waterPeriod);
        setGardenId(gardenId);

        setNextWaterDate();
        setImageFile(new File("./src/images/default.png"));
    }

    /**
     * creates plant object with a given image
     * @param plantName
     * @param description
     * @param plantingDate
     * @param lastWaterDate
     * @param waterPeriod
     * @param gardenId
     * @param imageFile
     * @throws IOException
     */
    public Plant(String plantName, String description, LocalDate plantingDate, LocalDate lastWaterDate, int waterPeriod, int gardenId, File imageFile) throws IOException {
        this(plantName,description,plantingDate,lastWaterDate,waterPeriod,gardenId);
        setImageFile(imageFile);
        copyImageFile();
    }

    /**
     * creates plant object for the needs of the table view of the home page
     * @param plantName
     * @param description
     * @param lastWaterDate
     * @param nextWaterDate
     */
    public Plant(String plantName,String description,LocalDate lastWaterDate,LocalDate nextWaterDate){
        this.plantName = plantName;
        this.description =description;
        this.lastWaterDate = lastWaterDate;
        this.nextWaterDate = nextWaterDate;

    }



    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        if(plantId>=-1){
        this.plantId = plantId;}
        else{
            throw new IllegalArgumentException("The plant id should be positive number!");
        }
    }

    public int getGardenId() {
        return gardenId;
    }

    public void setGardenId(int gardenId) {
        this.gardenId = gardenId;
    }

    public int getWaterPeriod() {
        return waterPeriod;
    }

    public void setWaterPeriod(int waterPeriod) {
        if(waterPeriod>=0){
        this.waterPeriod = waterPeriod;}
        else{
            throw new IllegalArgumentException("The water period should be positive number");
        }
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public LocalDate getLastWaterDate() {
        return lastWaterDate;
    }

    public void setLastWaterDate(LocalDate lastWaterDate) {
        if(lastWaterDate.isAfter(plantingDate) || lastWaterDate.isEqual(plantingDate)){
        this.lastWaterDate = lastWaterDate;}
        else{
            throw new IllegalArgumentException("The last water date should be after or at the time of planting!");
        }
    }

    public void setNextWaterDate(){
        this.nextWaterDate = lastWaterDate.plusDays(waterPeriod);
    }

    public LocalDate getNextWaterDate(){
        return this.nextWaterDate;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }


    /**
     * This method will copy the file specified to the images directory on this server and give it
     * a unique name
     */
    public void copyImageFile() throws IOException
    {
        //create a new Path to copy the image into a local directory
        Path sourcePath = imageFile.toPath();

        String uniqueFileName = getUniqueFileName(imageFile.getName());

        Path targetPath = Paths.get("./src/images/"+uniqueFileName);

        //copy the file to the new directory
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        //update the imageFile to point to the new File
        imageFile = new File(targetPath.toString());
    }


    /**
     * This method will receive a String that represents a file name and return a
     * String with a random, unique set of letters prefixed to it
     */
    private String getUniqueFileName(String oldFileName)
    {
        String newName;

        //create a Random Number Generator
        SecureRandom rng = new SecureRandom();

        //loop until we have a unique file name
        do
        {
            newName = "";

            //generate 32 random characters
            for (int count=1; count <=32; count++)
            {
                int nextChar;

                do
                {
                    nextChar = rng.nextInt(123);
                } while(!validCharacterValue(nextChar));

                newName = String.format("%s%c", newName, nextChar);
            }
            newName += oldFileName;

        } while (!uniqueFileInDirectory(newName));

        return newName;
    }


    /**
     * This method will search the images directory and ensure that the file name
     * is unique
     */
    public boolean uniqueFileInDirectory(String fileName)
    {
        File directory = new File("./src/images/");

        File[] dir_contents = directory.listFiles();

        for (File file: dir_contents)
        {
            if (file.getName().equals(fileName))
                return false;
        }
        return true;
    }

    /**
     * This method will validate if the integer given corresponds to a valid
     * ASCII character that could be used in a file name
     */
    public boolean validCharacterValue(int asciiValue)
    {

        //0-9 = ASCII range 48 to 57
        if (asciiValue >= 48 && asciiValue <= 57)
            return true;

        //A-Z = ASCII range 65 to 90
        if (asciiValue >= 65 && asciiValue <= 90)
            return true;

        //a-z = ASCII range 97 to 122
        if (asciiValue >= 97 && asciiValue <= 122)
            return true;

        return false;
    }

    /**
     * This method will write the instance of the Plant into the database
     */
    public void insertIntoDB() throws SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try
        {
            //1. Connect to the database
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

            //2. Create a String that holds the query with ? as user inputs
            String sql = "insert into plants(plant_name,description,planting_date,last_water_date,next_water_date,water_period,imageFile,garden_id) " +
                    "values (?,?,?,?,?,?,?,?)";
                    //3. prepare the query
            preparedStatement = conn.prepareStatement(sql);

            //4. Convert the LocalDate into a SQL date
            Date pd = Date.valueOf(plantingDate);
            Date lwd = Date.valueOf(lastWaterDate);
            Date nwd = Date.valueOf(nextWaterDate);

            //5. Bind the values to the parameters
            preparedStatement.setString(1, plantName);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, pd);
            preparedStatement.setDate(4,lwd);
            preparedStatement.setDate(5,nwd);
            preparedStatement.setInt(6,waterPeriod);
            preparedStatement.setString(7, imageFile.getName());
            preparedStatement.setInt(8,gardenId);

            preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (preparedStatement != null)
                preparedStatement.close();

            if (conn != null)
                conn.close();
        }
    }


    /**
     * This will update the plant in the database
     */
    public void updateIntoDB() throws SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try{
            //1.  connect to the DB
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

            //2.  create a String that holds our SQL update command with ? for user inputs
            String sql = "update plants \n" +
                    "set plant_name = ?, description=?, planting_date=?, last_water_date=?, next_water_date=?, water_period=?, imageFile=?, garden_id=?\n" +
                    "where plant_id = ?";

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);

            //4.  convert the LocalDate into a date object
            Date pd = Date.valueOf(plantingDate);
            Date lwd = Date.valueOf(lastWaterDate);
            Date nwd = Date.valueOf(nextWaterDate);

            //5. bind the parameters
            preparedStatement.setString(1, plantName);
            preparedStatement.setString(2,description);
            preparedStatement.setDate(3,pd);
            preparedStatement.setDate(4,lwd);
            preparedStatement.setDate(5,nwd);
            preparedStatement.setInt(6,waterPeriod);
            preparedStatement.setString(7,imageFile.getName());
            preparedStatement.setInt(8,gardenId);
            preparedStatement.setInt(9,plantId);



            //6. run the command on the SQL server
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (conn != null)
                conn.close();
            if (preparedStatement != null)
                preparedStatement.close();
        }

    }

    public void deleteFromDB() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try{
            //1.  connect to the DB
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

            //2.  create a String that holds our SQL update command with ? for user inputs
            String sql = "delete from plants where plant_id = ?";

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);

            //4.  convert the LocalDate into a date object


            //5. bind the parameters
            preparedStatement.setInt(1, getPlantId());




            //6. run the command on the SQL server
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (conn != null)
                conn.close();
            if (preparedStatement != null)
                preparedStatement.close();
        }

    }

    public void updateLastWaterDateInDB(int id,LocalDate nextWaterDate) throws SQLException
    {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try{
            //1.  connect to the DB
            conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

            //2.  create a String that holds our SQL update command with ? for user inputs
            String sql = String.format("UPDATE plants SET last_water_date=date(Now()), next_water_date=? WHERE plant_id = %d",id);

            //3. prepare the query against SQL injection
            preparedStatement = conn.prepareStatement(sql);

            //4.  convert the birthday into a date object


            Date nwd = Date.valueOf(nextWaterDate);

            //5. bind the parameters


            preparedStatement.setDate(1, nwd);



            //6. run the command on the SQL server
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (conn != null)
                conn.close();
            if (preparedStatement != null)
                preparedStatement.close();
        }

    }

    @Override
    public String toString() {
        return getPlantName();
    }
}
