package com.cardiag.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.cardiag.models.commands.ObdCommand;
import com.cardiag.models.commands.entities.Category;
import com.cardiag.models.solutions.NoErrorSolution;
import com.cardiag.models.solutions.NoSolution;
import com.cardiag.models.solutions.Solution;
import com.cardiag.models.solutions.Step;
import com.cardiag.models.solutions.TroubleCode;
import com.cardiag.persistence.StepsContract.StepEntry;
import com.cardiag.persistence.StepsContract.StepSolEntry;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.cardiag.persistence.ObdCommandContract.CommandEntry;
import static com.cardiag.persistence.TroubleCodesContract.*;
import static com.cardiag.persistence.StepsContract.*;

/**
 * Created by lrocca on 26/07/2017.
 */
public class DataBaseService extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "CarDiag.db";
    public static String DATABASE_PATH;

    private SQLiteDatabase cardiagDB;
    private final Context context;

    /**
     * Descomentar el metodo deleteDataBase cuando hayan
     * nuevos datos en la base de datos (Los estaticos)
     */
    public DataBaseService(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        DATABASE_PATH = context.getFilesDir().getParentFile().getPath()
                + "/databases/";

        createDataBase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void createDataBase() {

        boolean dbExist = checkDataBase();

        if(!dbExist){
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                //throw new Error("Error copying database");
            }
        }
        else {
            checkVersion();
        }
    }

    private void checkVersion(){
        cardiagDB = this.getReadableDatabase();
        String version = "0";

        String[] projection = {
               "version"
        };

        Cursor c = cardiagDB.query(
                "version",                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,//   selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null//     sortOrder                                 // The sort order
        );

        if (c.moveToFirst()) {
            if(c.getInt(0) < DATABASE_VERSION){
                //uptdate database with new version
                deleteDataBase();
                createDataBase();
            }
        }
    }

    private void deleteDataBase() {
        boolean dbExist = checkDataBase();
        if(dbExist){
            context.deleteDatabase(DATABASE_NAME);
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){
            //database does't exist yet.
        }

        if(checkDB != null){
            checkDB.close();
        }

        return (checkDB != null);
    }

    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DATABASE_PATH + DATABASE_NAME;
        cardiagDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if(this.cardiagDB != null){
            this.cardiagDB.close();
        }

        super.close();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<Step> getSteps(int id) {
        if(id ==10) return this.getStepsDummy(id);
        cardiagDB = this.getReadableDatabase();
        ArrayList<Step> ls = new ArrayList<Step>();//ls.add(new Step("s0101","Descripcion"));

        String[] projection = {
                StepEntry._ID,
                StepEntry.PATH,
                StepEntry.DESCRIPTION
                //       , StepEntry.ORDER
        };
        // Filter results WHERE "title" = 'My Title'
        String ssQuery = " (select "+ StepSolEntry.IDSTEP+" FROM " + StepSolEntry.TABLE_NAME + " WHERE "+StepSolEntry.IDSOL+" = "+String.valueOf(id)+" )";
        String selection = StepEntry._ID + " IN "+ssQuery;

        // How you want the results sorted in the resulting Cursor//  String sortOrder = StepEntry.ORDER + " ASC";
        Cursor c = cardiagDB.query(
                StepEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                null,//   selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null//     sortOrder                                 // The sort order
        );
        int index=0;
        if (c.moveToFirst()) {
            do {
                String img= c.getString(1).replace(".jpg","");
                Step  step = new Step( img,c.getString(2));
                step.setPosition(index); index++;
                ls.add(step);
            } while(c.moveToNext());
        }
        cardiagDB.close();
        return ls;
    }

    public String dummyQuery(String s) {
        cardiagDB = this.getReadableDatabase();
        String[] projection = {
                StepSolEntry._ID,
                StepSolEntry.IDSOL,
                StepSolEntry.IDSTEP
        };
        Cursor c = cardiagDB.query(StepSolEntry.TABLE_NAME, projection,null, null, null, null, null);
        s=" (select "+ StepSolEntry.IDSTEP+" FROM " + StepSolEntry.TABLE_NAME ;
        s="0";
        if (c.moveToFirst()) {
            do {
                s=s+"-"+c.getInt(0)+"/"+c.getInt(1)+"/"+c.getInt(2);
            } while(c.moveToNext());
        }
        cardiagDB.close();

        return s;
    }


    //////////////////////Commands///////////////////////////

    public ArrayList<ObdCommand> getCommands(String whereColumns, String[] whereColumnsValues) {
        cardiagDB = this.getReadableDatabase();
        ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();

        String[] projection = {
                CommandEntry._ID,
                        CommandEntry.NAME,
                        CommandEntry.SELECTED
        };

        Cursor c = cardiagDB.query(
                CommandEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                whereColumns,                                // The columns for the WHERE clause
                whereColumnsValues,//   selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null//     sortOrder                                 // The sort order
        );

        if (c.moveToFirst()) {
            do {
                ObdCommand cmd = ObdCommandRowMapper.getCommand(c);
                if (cmd != null) {
                    cmds.add(cmd);
                }
            } while(c.moveToNext());
        }
        return cmds;
    }

    public void insertCommand(ObdCommand cmd) {
        cardiagDB = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CommandEntry._ID, cmd.getCmd());
        values.put(CommandEntry.NAME, cmd.getName());

        Integer selected = (cmd.getSelected()) ? 1 : 0;
        values.put(CommandEntry.SELECTED, selected);

        cardiagDB.insert(CommandEntry.TABLE_NAME, null, values);

        cardiagDB.close();
    }

    public void updateCommand(ObdCommand cmd, Boolean available) {
        cardiagDB = this.getWritableDatabase();

        Integer sel = (cmd.getSelected()) ? 1 : 0;
        Integer avble;
        ContentValues cv = new ContentValues();

        if (available != null) {
            avble =(available) ? 1 : 0;
            cv.put(CommandEntry.AVAILABILITY, avble);
        }
        cv.put(CommandEntry.SELECTED,sel);

        String[] whereValues = new String[] {cmd.getCmd()};

        cardiagDB.update(CommandEntry.TABLE_NAME, cv, CommandEntry._ID+"=?",whereValues);
    }

    public void resetAvailability() {
        cardiagDB = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CommandEntry.AVAILABILITY,0);

        cardiagDB.update(CommandEntry.TABLE_NAME, cv, null,null);
    }

    public void resetSelection() {
        ContentValues cv = new ContentValues();
        cv.put(CommandEntry.SELECTED,0);

        cardiagDB.update(CommandEntry.TABLE_NAME, cv, null,null);
    }

    public void setSelection() {
        ContentValues cv = new ContentValues();
        cv.put(CommandEntry.SELECTED,1);

        cardiagDB.update(CommandEntry.TABLE_NAME, cv, null,null);
    }

    public ArrayList<ObdCommand> getCommandsRaWQuery(String query, String[] args) {
        cardiagDB = this.getReadableDatabase();
        ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();

        Cursor c = cardiagDB.rawQuery(query, args);

        if (c.moveToFirst()) {
            do {
                ObdCommand cmd = ObdCommandRowMapper.getCommand(c);
                if (cmd != null) {
                    cmds.add(cmd);
                }
            } while(c.moveToNext());
        }
        return cmds;
    }

    public ArrayList<ObdCommand> getCategoryCommands(String[] groups) {
        cardiagDB = this.getReadableDatabase();
        ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();

        String[] projection = {
                "cmd."+ CommandEntry._ID,
                "cmd."+ CommandEntry.NAME,
                "cmd."+ CommandEntry.SELECTED
        };

        String where = "g."+ CategoryContract.CategoryEntry.NAME+" = ? and " + "g."+ CategoryContract.CategoryEntry._ID + " = cxg." + CommandCategoryContract.CommandCategoryEntry.GROUP;
        where += " and cmd." + CommandEntry._ID + " = cxg." + CommandCategoryContract.CommandCategoryEntry.COMMAND + " and cmd." + CommandEntry.AVAILABILITY + " = 1";
        String[] whereValues = groups;

        Cursor c = cardiagDB.query(
                CommandEntry.TABLE_NAME + " as cmd, " + CategoryContract.CategoryEntry.TABLE_NAME + " as g, " + CommandCategoryContract.CommandCategoryEntry.TABLE_NAME + " as cxg",                     // The table to query
                projection,                               // The columns to return
                where,                                // The columns for the WHERE clause
                whereValues,//   selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null//     sortOrder                                 // The sort order
        );

        if (c.moveToFirst()) {
            do {
                ObdCommand cmd = ObdCommandRowMapper.getCommand(c);
                if (cmd != null) {
                    cmds.add(cmd);
                }
            } while(c.moveToNext());
        }
        return cmds;
    }

    public ArrayList<Category> getGroups(String whereColumns, String[] whereColumnsValues) {
        cardiagDB = this.getReadableDatabase();
        ArrayList<Category> groups = new ArrayList<Category>();

        String[] projection = {
                CategoryContract.CategoryEntry._ID,
                CategoryContract.CategoryEntry.NAME
        };

        Cursor c = cardiagDB.query(
                CategoryContract.CategoryEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                whereColumns,                                // The columns for the WHERE clause
                whereColumnsValues,//   selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null//     sortOrder                                 // The sort order
        );

        if (c.moveToFirst()) {
            do {
                Category g = new Category(c.getInt(0), c.getString(1));
                groups.add(g);
            } while(c.moveToNext());
        }
        return groups;
    }

    //////////////////////End Commands///////////////////////////

    public ArrayList<Solution> getSolutions(TroubleCode troubleCode, SQLiteDatabase cardiagDB) {

        ArrayList<Solution> solutions = new ArrayList<>();

        String[] projection = {
                SolutionEntry._ID,
                SolutionEntry.NAME, //       SolutionEntry.DESCRIPTION,
                SolutionEntry.PRIORITY
        };
        String ssQuery = " (select "+ SolCodeseEntry.IDSOL+" FROM " + SolCodeseEntry.TABLE_NAME + " WHERE "+SolCodeseEntry.IDCODE+" = "+String.valueOf(troubleCode.getId())+" )";
        String selection = SolutionEntry._ID + " IN "+ssQuery;

       Cursor c = cardiagDB.query(
                SolutionEntry.TABLE_NAME, projection, selection,null, null, null, null
        );

        if (c.moveToFirst()) {
            do {
                solutions.add(new Solution(c.getString(1),c.getInt(2),c.getInt(0)));
            } while(c.moveToNext());
        }else {
            solutions.add(new NoSolution());
            solutions.add(new NoErrorSolution(troubleCode.getName()));
        }
        /* DUMMY
        switch (troubleCode.getName()){
            case "P0001":
                solutions.add(new NoSolution());
                break;
            case "P0008":
                solutions.add(new NoErrorSolution("P0008"));
                break;
            default : solutions.add(new Solution("Solucion Cambie el aceite",null));
                solutions.add(new Solution("Chequear motor",null));
                break;
        }*/
        return solutions;
    }

    public ArrayList<Step> getStepsDummy(int id) {
        ArrayList<Step> steps = new ArrayList<Step>();
        steps.add(new Step("logo","Cardiag \nTutorial"));
        steps.add(new Step("s10s54","Tome su dispositivo OBD para conectarlo."));
        steps.add(new Step("s10s55","Proceda a conectarlo en la placa ECU de su vehículo. La entrada OBD2 suele encontrarse debajo del volante como se muestra en la imágen."));
        steps.add(new Step("s10s56","Verifique la correcta conexión. En caso de que vea la luz roja parpadeando, eso indica que el mismo ya está funcionando."));
    //    steps.add(new Step("s10s57","Tome su dispositivo celular y diríjase a AJUSTES para configurar el dispositivo."));
        steps.add(new Step("s10s58","Ingrese al menú BLUETOOTH de su dispositivo movil (por fuera de la aplicación)."));
        steps.add(new Step("s10s59","Busque los dispositivos disponibles, seleccione el OBD2 que acaba de conectar. Si no aparece revise la conexión."));
        steps.add(new Step("s10s60","Ingrese el PIN para la vinculación si es que la configuro, caso contrario elija una en ese momento."));
       // steps.add(new Step("s10s61","El dispositivo OBD2 ya debe estar vinculado a su teléfono móvil y debe verse reflejado."));
      //  steps.add(new Step("s10s62","Ingrese a la aplicación CARDIAG. En el menú superior se van a desplegar algunas opciones que debe configurar como primer paso."));
        steps.add(new Step("s10s63","Seleccione 'Elegir Dispositivo OBD2' del menú para vincular el aparato a la aplicación."));
     //   steps.add(new Step("s10s64","Allí se van a listar los dispositivos que están vinculados al teléfono. Tildamos el correspondiente al OBD2 para vincularlo con la aplicación CARDIAG."));
     //   steps.add(new Step("s10s65","Una vez hecho esto, ya está operativa la aplicación. Puede navegar entre los distintos menues y conocer el estado de su vehículo."));
        return steps;
    }
    public TroubleCode getTroubleCode(String id) {
        cardiagDB = this.getReadableDatabase();
        TroubleCode tbCode;
        ArrayList<Solution> solutions = new ArrayList<>();

        String[] projection = {
                TroubleEntry._ID,
                TroubleEntry.NAME,
                TroubleEntry.DESCRIPTION
        };

       String selection = TroubleEntry.NAME + " = '"+id+"'";
       // selection="1!=1";
        Cursor c = cardiagDB.query(
                TroubleEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,null, null, null, null
        );
        if (c.moveToFirst()) {
            tbCode=new TroubleCode(c.getString(1),c.getString(2),c.getInt(0));
            solutions.addAll(this.getSolutions(tbCode,cardiagDB));
        }else{
            tbCode=new TroubleCode(id,"", -1);
            solutions.add(new NoErrorSolution(id));
            solutions.add(new NoSolution());
        }
        tbCode.setSolutions(solutions);
        cardiagDB.close();

        return tbCode;
    }
}
