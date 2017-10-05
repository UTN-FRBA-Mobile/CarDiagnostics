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
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CarDiag.db";
    public static String DATABASE_PATH;

    private SQLiteDatabase cardiagDB;
    private final Context context;

    /**
     * Descomentar el metodo deleteDataBase cuando hayan
     * nuevos datos en la base de datos (Los estaticos)
     */
    public DataBaseService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        DATABASE_PATH = context.getFilesDir().getParentFile().getPath()
                + "/databases/";

        //deleteDataBase();
        createDataBase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //createDataBase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            deleteDataBase();
            createDataBase();
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


    /*private void insertSteps(ArrayList<Step> steps) {
        for (Step s : steps){
            this.insertStep(s);
        }
    }*/

   /* public void insertSolution(Solution solution) {
        this.insertSteps(solution.getSteps());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SolutionEntry.DESCRIPTION, " ");
        values.put(SolutionEntry.NAME, solution.getName());
        values.put(SolutionEntry.PRIORITY,String.valueOf(solution.getPriority()));
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(SolutionEntry.TABLE_NAME, null, values);
        solution.setId(newRowId);
        this.insertStepSolutions(solution,db);
        db.close();
    }*/

    /*private void insertStepSolutions(Solution solution, SQLiteDatabase db) {
        String idsol = String.valueOf(solution.getId());
        for (int i =0; i<solution.getSteps().size();i++){
            Step step = solution.getSteps().get(i);
            ContentValues values = new ContentValues();
            values.put(StepSolEntry.IDSOL, idsol);
            values.put(StepSolEntry.IDSTEP, String.valueOf(step.getId()));
            values.put(StepSolEntry.ORDER, String.valueOf(i));
            db.insert(StepSolEntry.TABLE_NAME, null, values);
        }
    }*/


    /*private void insertStep(Step step) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StepEntry.DESCRIPTION, step.getDesc());
        values.put(StepEntry.PATH, step.getImgId());
        //      values.put(StepEntry.ORDER,String.valueOf(step.getPosition()));
        long newRowId = db.insert(StepEntry.TABLE_NAME, null, values);
        step.setId(newRowId);
        db.close();
    }*/


    public ArrayList<Step> getSteps(int id) {
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

    /*public void insertDummySol() {
        this.insertSolution(this.getDummySol());
    }
    private Solution getDummySol() {
        ArrayList<Step> steps  = new ArrayList<Step>();
        steps.add(new Step("s0101","Revisar cuidadosamente el circuito del banco 1 / circuito de VCT(sincronización variable del árbol de levas)"+
                ", sistema de cableado y conectores, como lo indica el manual de reparación"));
        steps.add( new Step("s0102","Con el motor caliente, cheque la operación de la OCV (válvula de control)"));
        steps.add( new Step("s0103","sustitución / repare o reemplace según sea necesario"));

        return  new Solution("Solucion", steps);
    }
    public void insertDummySteps() {
        this.insertStep(new Step("s0101","Revisar cuidadosamente el circuito del banco 1 / circuito de VCT(sincronización variable del árbol de levas)"+
                ", sistema de cableado y conectores, como lo indica el manual de reparación"));
        this.insertStep( new Step("s0102","Con el motor caliente, cheque la operación de la OCV (válvula de control)"));
        this.insertStep( new Step("s0103","sustitución / repare o reemplace según sea necesario"));
    }*/


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

    public ArrayList<ObdCommand> getGroupCommands(String[] groups) {
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
     //   steps.add(new Step("ic_btcar","Cardiag"));
        steps.add(new Step("s1s1","Abrir el capot del auto."));
        steps.add(new Step("s1s2","Identifique la bateria dentro del capot. Generalmente su ubicacion esta normalizada y es la que se muestra en la imagen. Puede manipularla sin problemas ya que es seguro."));
        steps.add(new Step("s1s3","Quitamos la protección para dejar la batería al descubierto e identificamos los dos bornes. Tanto el positivo como el negativo deben estar identificados con un signo según su polaridad."));
        steps.add(new Step("s1s4","Quitamos cada uno de los bornes usando una llave inglesa (normalmente del número 10) para aflojar las tuercas que los sostienen."));
        steps.add(new Step("s1s5","Una vez hecho esto, la batería quedará suelta eléctricamente. El siguiente paso es soltarla mecánicamente."));
        steps.add(new Step("s1s6","Para esto hay que desligarla del coche aflojando un tornillo ubicado donde se señala en la imagen."));
        steps.add(new Step("s1s7","Retirar el tornillo señalado usando una llave (generalmente del número 13). Así se desvinculará la chapa que sostiene la batería."));
        steps.add(new Step("s1s8","Retirar la placa que sujeta la batería."));
        steps.add(new Step("s1s9","La batería ya se encuentra suelta, procedemos a retirarla."));
        steps.add(new Step("s1s10","Comprar una batería de iguales características (Igual medida, voltios y amperios/hora)."));
        steps.add(new Step("s1s11","Colocar la nueva batería donde se encontraba la anterior."));
        steps.add(new Step("s1s12","Volver a colocar la placa que sujeta la batería y el tornillo correspondiente."));
        steps.add(new Step("s1s13","Lijar los contactos con una lima o lija hasta que queden brillantes."));
        steps.add(new Step("s1s14","Conectar la batería y volver a colocar la protección en la misma."));
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
        }
        tbCode.setSolutions(solutions);
        cardiagDB.close();

        return tbCode;
    }
}
