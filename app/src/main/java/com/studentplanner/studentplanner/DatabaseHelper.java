package com.studentplanner.studentplanner;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.studentplanner.studentplanner.models.Classes;
import com.studentplanner.studentplanner.models.Coursework;
import com.studentplanner.studentplanner.models.ImageHandler;
import com.studentplanner.studentplanner.models.Module;
import com.studentplanner.studentplanner.models.ModuleTeacher;
import com.studentplanner.studentplanner.models.Semester;
import com.studentplanner.studentplanner.models.Student;
import com.studentplanner.studentplanner.models.Teacher;
import com.studentplanner.studentplanner.tables.ClassTable;
import com.studentplanner.studentplanner.tables.CourseworkTable;
import com.studentplanner.studentplanner.tables.ModuleTable;
import com.studentplanner.studentplanner.tables.ModuleTeacherTable;
import com.studentplanner.studentplanner.tables.SemesterTable;
import com.studentplanner.studentplanner.tables.StudentTable;
import com.studentplanner.studentplanner.tables.TeacherTable;
import com.studentplanner.studentplanner.utils.AccountPreferences;
import com.studentplanner.studentplanner.utils.CalendarUtils;
import com.studentplanner.studentplanner.utils.Encryption;
import com.studentplanner.studentplanner.utils.Helper;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentPlanner.db";
    private static final int DATABASE_VERSION = 1;

    private final SQLiteDatabase db;
    private static DatabaseHelper instance;
    private final Context context;
    private static final String ERROR_TAG = "ERROR";

    private String getErrorMessage(Exception e) {
        Helper.longToastMessage(context, String.format("Error in %s class\nplease refer to the logcat\nfor more details", DatabaseHelper.getInstance(context).getClass().getSimpleName()));
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        return MessageFormat.format("There was a problem in method: {0}\nError: \n{1}", methodName, e.getMessage());

    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
        db = getWritableDatabase();
    }

    private int getStudentID() {
        return AccountPreferences.getStudentID(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createAllTables(db);
        addDefaultTableValues(db);
    }


    private void dropTables(SQLiteDatabase db, String... tableNames) {
        Arrays.stream(tableNames).toList().forEach(table -> db.execSQL("DROP TABLE IF EXISTS " + table));
    }

    private void dropAllTables(SQLiteDatabase db) {

        dropTables(db,
                ClassTable.TABLE_NAME,
                CourseworkTable.TABLE_NAME,
                ModuleTable.TABLE_NAME,
                ModuleTeacherTable.TABLE_NAME,
                SemesterTable.TABLE_NAME,
                StudentTable.TABLE_NAME,
                TeacherTable.TABLE_NAME
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAllTables(db);
        Log.d(getClass().getName().toUpperCase() + "_UPGRADE", MessageFormat.format("{0} database upgrade to version {1}  - old data lost", DATABASE_NAME, newVersion));
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void addDefaultTableValues(SQLiteDatabase db) {
        addStudent(db);
    }


    private void createAllTables(SQLiteDatabase db) {
        createStudentTable(db);

        createModuleTable(db);
        createCourseworkTable(db);

        createTeacherTable(db);
        createModuleTeacherTable(db);

        createSemesterTable(db);
        createClassesTable(db);

    }


    private void createStudentTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + StudentTable.TABLE_NAME + " ("
                + StudentTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StudentTable.COLUMN_FIRSTNAME + " TEXT NOT NULL,"
                + StudentTable.COLUMN_LASTNAME + " TEXT NOT NULL,"
                + StudentTable.COLUMN_EMAIL + " TEXT NOT NULL UNIQUE,"
                + StudentTable.COLUMN_PHONE + " TEXT,"
                + StudentTable.COLUMN_PASSWORD + " TEXT NOT NULL,"
                + StudentTable.COLUMN_REGISTERED_DATE + " TEXT NOT NULL"
                + ");"
        );

    }

    private void createModuleTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ModuleTable.TABLE_NAME + " ("
                + ModuleTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ModuleTable.COLUMN_STUDENT_ID + " INTEGER NOT NULL,"
                + ModuleTable.COLUMN_MODULE_C0DE + " TEXT NOT NULL,"
                + ModuleTable.COLUMN_MODULE_NAME + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + ModuleTable.COLUMN_STUDENT_ID + ") REFERENCES " + StudentTable.TABLE_NAME + "(" + StudentTable.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE "
                + ");"
        );

    }

    private void createCourseworkTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CourseworkTable.TABLE_NAME + " ("
                + CourseworkTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CourseworkTable.COLUMN_MODULE_ID + " INTEGER NOT NULL,"
                + CourseworkTable.COLUMN_TITLE + " TEXT NOT NULL,"
                + CourseworkTable.COLUMN_DESCRIPTION + " TEXT, "
                + CourseworkTable.COLUMN_PRIORITY + " TEXT NOT NULL, "
                + CourseworkTable.COLUMN_DEADLINE + " TEXT NOT NULL, "
                + CourseworkTable.COLUMN_DEADLINE_TIME + " TEXT NOT NULL, "
                + CourseworkTable.COLUMN_COMPLETED + " TEXT NOT NULL DEFAULT 'No', "
                + CourseworkTable.COLUMN_IMAGE + " BLOB, "
                + "FOREIGN KEY (" + CourseworkTable.COLUMN_MODULE_ID + ") REFERENCES " + ModuleTable.TABLE_NAME + "(" + ModuleTable.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE"
                + ");"
        );

    }

    private void createTeacherTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TeacherTable.TABLE_NAME + " ("
                + TeacherTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TeacherTable.COLUMN_STUDENT_ID + " INTEGER NOT NULL,"
                + TeacherTable.COLUMN_FIRSTNAME + " TEXT NOT NULL,"
                + TeacherTable.COLUMN_LASTNAME + " TEXT NOT NULL,"
                + TeacherTable.COLUMN_EMAIL + " TEXT NOT NULL UNIQUE,"
                + "FOREIGN KEY (" + TeacherTable.COLUMN_STUDENT_ID + ") REFERENCES " + StudentTable.TABLE_NAME + "(" + StudentTable.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE"
                + ");"
        );

    }

    private void createModuleTeacherTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + ModuleTeacherTable.TABLE_NAME + " ("
                + ModuleTeacherTable.COLUMN_TEACHER_ID + " INTEGER NOT NULL, "
                + ModuleTeacherTable.COLUMN_MODULE_ID + " INTEGER NOT NULL, "
                + "PRIMARY KEY(" + ModuleTeacherTable.COLUMN_TEACHER_ID + ", " + ModuleTeacherTable.COLUMN_MODULE_ID + "),"
                + "FOREIGN KEY (" + ModuleTeacherTable.COLUMN_TEACHER_ID + ") REFERENCES " + TeacherTable.TABLE_NAME + "(" + TeacherTable.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + "FOREIGN KEY (" + ModuleTeacherTable.COLUMN_MODULE_ID + ") REFERENCES " + ModuleTable.TABLE_NAME + "(" + ModuleTable.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE"
                + ");"
        );

    }


    public void createSemesterTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SemesterTable.TABLE_NAME + " ("
                + SemesterTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SemesterTable.COLUMN_STUDENT_ID + " INTEGER NOT NULL,"
                + SemesterTable.COLUMN_NAME + " TEXT NOT NULL,"
                + SemesterTable.COLUMN_START_DATE + " TEXT NOT NULL, "
                + SemesterTable.COLUMN_END_DATE + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + SemesterTable.COLUMN_STUDENT_ID + ") REFERENCES " + StudentTable.TABLE_NAME + "(" + StudentTable.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE"
                + ");"
        );

    }


    public void createClassesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ClassTable.TABLE_NAME + " ("
                + ClassTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ClassTable.COLUMN_MODULE_ID + " INTEGER NOT NULL,"
                + ClassTable.COLUMN_SEMESTER_ID + " INTEGER NOT NULL,"
                + ClassTable.COLUMN_DOW + " INTEGER NOT NULL, "
                + ClassTable.COLUMN_START_TIME + " TEXT NOT NULL, "
                + ClassTable.COLUMN_END_TIME + " TEXT NOT NULL, "
                + ClassTable.COLUMN_ROOM + " TEXT, "
                + ClassTable.COLUMN_TYPE + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + ClassTable.COLUMN_MODULE_ID + ") REFERENCES " + ModuleTable.TABLE_NAME + "(" + ModuleTable.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + "FOREIGN KEY (" + ClassTable.COLUMN_SEMESTER_ID + ") REFERENCES " + SemesterTable.TABLE_NAME + "(" + SemesterTable.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE"
                + ");"
        );

    }


    public boolean columnExists(String fieldValue, String column, String table) {
        String[] columns = {column};
        SQLiteDatabase db = getReadableDatabase();
        String selection = column + " LIKE ?";
        String[] selectionArgs = {fieldValue};
        try (Cursor c = db.query(table, columns, selection, selectionArgs, null, null, null)) {
            return c.getCount() > 0;
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return false;
        }

    }

    @SuppressLint("Range")
    public boolean emailExists(String email, String excludedEmail) {
        String[] columns = {TeacherTable.COLUMN_EMAIL, TeacherTable.COLUMN_STUDENT_ID};
        String emailColumn = TeacherTable.COLUMN_EMAIL;
        SQLiteDatabase db = getReadableDatabase();
        String selection = emailColumn + " LIKE ?"
                + " AND " + emailColumn
                + " NOT LIKE ?"
                + "AND " + ModuleTable.COLUMN_STUDENT_ID + "= ?";

        String[] selectionArgs = {
                email,
                excludedEmail,
                String.valueOf(getStudentID()),

        };
        try (Cursor c = db.query(TeacherTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null)) {

            return c.getCount() > 0;

        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return false;

        }

    }


    @SuppressLint("Range")
    public boolean isAuthorised(String email, String password) {
        String[] columns = {StudentTable.COLUMN_ID};
        SQLiteDatabase db = getReadableDatabase();
        String selection = StudentTable.COLUMN_EMAIL + " = ?" + " AND " + StudentTable.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        try (Cursor c = db.query(StudentTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null)) {
            return c.getCount() > 0;
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return false;

        }

    }

    @SuppressLint("Range")
    public boolean moduleCodeExists(String moduleCode) {
        String[] columns = {ModuleTable.COLUMN_MODULE_C0DE, ModuleTable.COLUMN_STUDENT_ID};
        SQLiteDatabase db = getReadableDatabase();
        String selection = ModuleTable.COLUMN_MODULE_C0DE + " LIKE ?" + " AND " + ModuleTable.COLUMN_STUDENT_ID + " = ?";
        String[] selectionArgs = {moduleCode, String.valueOf(getStudentID())};

        try (Cursor c = db.query(ModuleTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null)) {
            return c.getCount() > 0;
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return false;

        }

    }


    @SuppressLint("Range")
    public boolean classExists(int moduleID, int semesterID, String type) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor c = db.rawQuery(
                """
                        SELECT
                            COUNT(*) class_exists
                        FROM classes c
                        JOIN modules m
                          ON m.module_id = c.module_id
                        WHERE m.student_id = ?
                        AND c.module_id = ?
                        AND semester_id = ?
                        AND type = ?
                        """,
                new String[]{
                        String.valueOf(getStudentID()),
                        String.valueOf(moduleID),
                        String.valueOf(semesterID),
                        type,
                })) {
            if (c.moveToFirst()) return c.getInt(0) > 0;

        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return false;

    }

    @SuppressLint("Range")
    public boolean moduleCodeExists(String moduleCode, String excludedModuleCode) {
        String[] columns = {ModuleTable.COLUMN_MODULE_C0DE, ModuleTable.COLUMN_STUDENT_ID};
        SQLiteDatabase db = getReadableDatabase();
        String selection = ModuleTable.COLUMN_MODULE_C0DE + " LIKE ?"
                + " AND " + ModuleTable.COLUMN_MODULE_C0DE
                + " NOT LIKE ?"
                + "AND " + ModuleTable.COLUMN_STUDENT_ID + "= ?";

        String[] selectionArgs = {
                moduleCode,
                excludedModuleCode,
                String.valueOf(getStudentID()),

        };
        try (Cursor c = db.query(ModuleTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null)) {

            return c.getCount() > 0;

        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return false;

        }

    }

    @SuppressLint("Range")
    public int getStudentID(String email) {
        int studentID = 0;
        String[] columns = {StudentTable.COLUMN_ID};
        SQLiteDatabase db = getReadableDatabase();
        String selection = StudentTable.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        try (Cursor c = db.query(StudentTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null)) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                studentID = c.getInt(0);
            }

        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return studentID;

        }
        return studentID;

    }

    @SuppressLint("Range")
    public String getStudentEmail(int studentID) {
        String email = "";
        String[] columns = {StudentTable.COLUMN_EMAIL};
        SQLiteDatabase db = getReadableDatabase();
        String selection = StudentTable.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(studentID)};

        try (Cursor c = db.query(StudentTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null)) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                email = c.getString(0);
            }

        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return email;

        }
        return email;

    }


    @SuppressLint("Range")
    public Student getUserFirstAndLastName(int userID) {
        String[] columns = {StudentTable.COLUMN_FIRSTNAME, StudentTable.COLUMN_LASTNAME};
        SQLiteDatabase db = getReadableDatabase();
        String selection = StudentTable.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userID)};
        Student student = new Student();

        try (Cursor c = db.query(StudentTable.TABLE_NAME, columns, selection, selectionArgs, null, null, null)) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    student.setFirstname(c.getString(c.getColumnIndex(StudentTable.COLUMN_FIRSTNAME)));
                    student.setLastname(c.getString(c.getColumnIndex(StudentTable.COLUMN_LASTNAME)));
                }

            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return student;

    }

    public boolean registerStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(StudentTable.COLUMN_FIRSTNAME, student.getFirstname());
        cv.put(StudentTable.COLUMN_LASTNAME, student.getLastname());
        cv.put(StudentTable.COLUMN_EMAIL, student.getEmail());
        if (student.getPhone() != null) {
            cv.put(StudentTable.COLUMN_PHONE, student.getPhone());
        }
        cv.put(StudentTable.COLUMN_PASSWORD, Encryption.encode(student.getPassword()));
        cv.put(StudentTable.COLUMN_REGISTERED_DATE, String.valueOf(CalendarUtils.getCurrentDate()));
        long result = db.insert(StudentTable.TABLE_NAME, null, cv);
        return result != -1;

    }

    public boolean addModule(Module module) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = Module.contentValues(module);
        cv.put(ModuleTable.COLUMN_STUDENT_ID, getStudentID());
        long result = db.insert(ModuleTable.TABLE_NAME, null, cv);
        return result != -1;

    }

    public boolean addSemester(Semester semester) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = Semester.contentValues(semester);
        cv.put(SemesterTable.COLUMN_STUDENT_ID, getStudentID());
        long result = db.insert(SemesterTable.TABLE_NAME, null, cv);
        return result != -1;

    }

    public boolean addCoursework(Coursework coursework) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = Coursework.contentValues(coursework);
        if (coursework.getImage() != null) {
            cv.put(CourseworkTable.COLUMN_IMAGE, ImageHandler.getBitmapAsByteArray(coursework.getImage()));

        }
        long result = db.insert(CourseworkTable.TABLE_NAME, null, cv);
        return result != -1;

    }

    public boolean addClass(Classes classes) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = Classes.contentValues(classes);
        long result = db.insert(ClassTable.TABLE_NAME, null, cv);
        return result != -1;

    }


    private void addStudent(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(StudentTable.COLUMN_REGISTERED_DATE, String.valueOf(CalendarUtils.getCurrentDate()));
        db.insert(StudentTable.TABLE_NAME, null, cv);

    }

    public boolean addTeacher(Teacher teacher) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = Teacher.contentValues(teacher);
        cv.put(TeacherTable.COLUMN_STUDENT_ID, getStudentID());
        long result = db.insert(TeacherTable.TABLE_NAME, null, cv);
        return result != -1;

    }

    public boolean addModuleTeacher(ModuleTeacher moduleTeacher) {
        return insertModuleTeacher(moduleTeacher);
    }

    private boolean insertModuleTeacher(ModuleTeacher moduleTeacher) {
        List<Long> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        moduleTeacher.teacherIDList().forEach(teacherID -> {
            ContentValues cv = new ContentValues();
            cv.put(ModuleTeacherTable.COLUMN_TEACHER_ID, teacherID);
            cv.put(ModuleTeacherTable.COLUMN_MODULE_ID, moduleTeacher.moduleID());
            long result = db.insert(ModuleTeacherTable.TABLE_NAME, null, cv);
            list.add(result);
        });

        return !list.contains((long) -1);
    }


    public boolean updateModuleTeacher(ModuleTeacher moduleTeacher) {
        if (!deleteSelectedTeacherModules(moduleTeacher.moduleID())) return false;
        return insertModuleTeacher(moduleTeacher);

    }

    @SuppressLint("Range")
    public List<Module> getModules() {
        List<Module> modules = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selection = ModuleTable.COLUMN_STUDENT_ID + " = ?";
        try (Cursor c = db.query(ModuleTable.TABLE_NAME, null, selection, getStudentIDArray(), null, null, null)) {
            if (!isCursorEmpty(c)) {
                while (c.moveToNext()) {
                    modules.add(new Module(
                            c.getInt(c.getColumnIndex(ModuleTable.COLUMN_ID)),
                            c.getString(c.getColumnIndex(ModuleTable.COLUMN_MODULE_C0DE)),
                            c.getString(c.getColumnIndex(ModuleTable.COLUMN_MODULE_NAME))
                    ));
                }
            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return modules;
    }


    @SuppressLint("Range")
    public ArrayList<Integer> getModuleTeacherByModuleID(int moduleID) {
        var teacherIds = new ArrayList<Integer>();
        SQLiteDatabase db = getReadableDatabase();
        String selection = ModuleTeacherTable.COLUMN_MODULE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(moduleID)};
        try (Cursor c = db.query(ModuleTeacherTable.TABLE_NAME, null, selection, selectionArgs, null, null, null)) {
            if (!isCursorEmpty(c)) {
                while (c.moveToNext()) {
                    teacherIds.add(c.getInt(c.getColumnIndex(ModuleTeacherTable.COLUMN_TEACHER_ID)));
                }
            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return teacherIds;
    }


    @SuppressLint("Range")
    public List<Teacher> getTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selection = TeacherTable.COLUMN_STUDENT_ID + " = ?";
        try (Cursor c = db.query(TeacherTable.TABLE_NAME, null, selection, getStudentIDArray(), null, null, null)) {
            if (!isCursorEmpty(c)) {
                while (c.moveToNext()) {
                    teachers.add(new Teacher(
                            c.getInt(c.getColumnIndex(TeacherTable.COLUMN_ID)),
                            c.getString(c.getColumnIndex(TeacherTable.COLUMN_FIRSTNAME)),
                            c.getString(c.getColumnIndex(TeacherTable.COLUMN_LASTNAME)),
                            c.getString(c.getColumnIndex(TeacherTable.COLUMN_EMAIL))

                    ));
                }
            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return teachers;
    }

    private String[] getStudentIDArray() {
        return new String[]{
                String.valueOf(getStudentID())
        };
    }

    @SuppressLint("Range")
    public List<Classes> getClasses() {
        List<Classes> classesList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        final String SQL = """
                SELECT
                    c.*
                FROM
                    classes c
                JOIN modules m ON
                    m.module_id = c.module_id
                WHERE
                    m.student_id = ?
                """;
        try (Cursor c = db.rawQuery(SQL, getStudentIDArray())) {
            if (!isCursorEmpty(c)) {
                while (c.moveToNext()) {

                    classesList.add(new Classes(

                            c.getInt(c.getColumnIndex(ClassTable.COLUMN_ID)),
                            c.getInt(c.getColumnIndex(ClassTable.COLUMN_MODULE_ID)),
                            c.getInt(c.getColumnIndex(ClassTable.COLUMN_SEMESTER_ID)),
                            c.getInt(c.getColumnIndex(ClassTable.COLUMN_DOW)),
                            LocalTime.parse(c.getString(c.getColumnIndex(ClassTable.COLUMN_START_TIME))),
                            LocalTime.parse(c.getString(c.getColumnIndex(ClassTable.COLUMN_END_TIME))),
                            c.getString(c.getColumnIndex(ClassTable.COLUMN_ROOM)),
                            c.getString(c.getColumnIndex(ClassTable.COLUMN_TYPE))


                    ));
                }
            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return classesList;
    }


    // manage add module teacher form
    @SuppressLint("Range")
    public List<Module> getModuleClassesAdd() {
        List<Module> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        final String SQL = "SELECT * FROM modules WHERE module_id NOT IN (SELECT module_id FROM module_teacher) AND student_id = ?";
        try (Cursor c = db.rawQuery(SQL, getStudentIDArray())) {
            if (isCursorEmpty(c)) Log.d(ERROR_TAG, "cursor is empty");
            while (c.moveToNext()) {
                list.add(new Module(
                        c.getInt(c.getColumnIndex(ModuleTable.COLUMN_ID)),
                        c.getString(c.getColumnIndex(ModuleTable.COLUMN_MODULE_C0DE)),
                        c.getString(c.getColumnIndex(ModuleTable.COLUMN_MODULE_NAME))
                ));
            }

        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return list;
    }

    public boolean deleteSelectedTeacherModules(int moduleID) {
        long result = db.delete(ModuleTeacherTable.TABLE_NAME, ModuleTeacherTable.COLUMN_MODULE_ID + "=?", new String[]{String.valueOf(moduleID)});
        return result != 0;

    }

    @SuppressLint("Range")
    public List<Coursework> getCoursework() {
        return courseworkList("""
                SELECT
                    c.*,
                    module_code,
                    module_name,
                    student_id
                FROM
                    coursework c
                JOIN modules m ON
                    m.module_id = c.module_id
                WHERE
                    student_id = ?
                """);
    }

    @SuppressLint("Range")
    private List<Coursework> courseworkList(final String SQL) {
        List<Coursework> courseworkList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery(SQL, getStudentIDArray())) {
            if (!isCursorEmpty(c)) {
                while (c.moveToNext()) {
                    Coursework coursework = new Coursework(
                            c.getInt(c.getColumnIndex(CourseworkTable.COLUMN_ID)),
                            c.getInt(c.getColumnIndex(CourseworkTable.COLUMN_MODULE_ID)),
                            c.getString(c.getColumnIndex(CourseworkTable.COLUMN_TITLE)),
                            c.getString(c.getColumnIndex(CourseworkTable.COLUMN_DESCRIPTION)),
                            c.getString(c.getColumnIndex(CourseworkTable.COLUMN_PRIORITY)),
                            LocalDate.parse(c.getString(c.getColumnIndex(CourseworkTable.COLUMN_DEADLINE))),
                            LocalTime.parse(c.getString(c.getColumnIndex(CourseworkTable.COLUMN_DEADLINE_TIME)))
                    );
                    coursework.setImage(c.getBlob(c.getColumnIndex(CourseworkTable.COLUMN_IMAGE)));
                    coursework.setCompleted(Coursework.isCompleted(c.getString(c.getColumnIndex(CourseworkTable.COLUMN_COMPLETED))));
                    courseworkList.add(coursework);

                }

            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return courseworkList;

    }

    @SuppressLint("Range")
    public List<Coursework> getUpComingCourseworkByMonth() {
        return courseworkList("""        
                SELECT c.*,
                       module_code,
                       module_name,
                       student_id
                FROM coursework c
                    JOIN modules m
                        ON m.module_id = c.module_id
                WHERE student_id = ?
                      AND c.deadline
                      BETWEEN DATE('now', 'start of month') AND DATE('now', 'start of month', '+1 month', '-1 day')
                ORDER by c.deadline DESC
                """);
    }


    @SuppressLint("Range")
    public List<ModuleTeacher> getModuleTeachers() {
        List<ModuleTeacher> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        final String SQL = """
                SELECT
                    mt.module_id,
                    GROUP_CONCAT(mt.teacher_id) teacher_id_list
                FROM
                    module_teacher mt
                JOIN modules m ON
                    m.module_id = mt.module_id
                WHERE
                    student_id = ?
                GROUP BY
                    mt.module_id
                """;
        try (Cursor c = db.rawQuery(SQL, getStudentIDArray())) {
            if (!isCursorEmpty(c)) {
                while (c.moveToNext()) {
                    int moduleID = c.getInt(c.getColumnIndex(ModuleTeacherTable.COLUMN_MODULE_ID));
                    String teacherIDs = c.getString(c.getColumnIndex("teacher_id_list"));
                    List<String> teacherIDListStr = new ArrayList<>(Arrays.asList(teacherIDs.split(",")));
                    List<Integer> TeacherIDList = Helper.convertStringArrayToIntArrayList(teacherIDListStr);
                    list.add(new ModuleTeacher(moduleID, TeacherIDList));
                }

            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return list;
    }

    @SuppressLint("Range")
    public List<String> getTeachersForSelectedModuleID(int moduleID) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        final String SQL = """
                SELECT
                    t.firstname,
                    t.lastname
                FROM
                    module_teacher mt
                JOIN teachers t ON
                    t.teacher_id = mt.teacher_id
                WHERE
                    module_id = ?
                                
                """;
        try (Cursor c = db.rawQuery(SQL, new String[]{String.valueOf(moduleID)})) {
            if (!isCursorEmpty(c)) {

                while (c.moveToNext()) {
                    String firstname = c.getString(c.getColumnIndex(TeacherTable.COLUMN_FIRSTNAME));
                    String lastname = c.getString(c.getColumnIndex(TeacherTable.COLUMN_LASTNAME));
                    list.add(String.format("%s %s", firstname, lastname));

                }

            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return list;
    }


    @SuppressLint("Range")
    public Coursework getSelectedCoursework(int id) {
        Coursework coursework = null;
        SQLiteDatabase db = getReadableDatabase();
        String selection = CourseworkTable.COLUMN_ID + " =?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor c = db.query(CourseworkTable.TABLE_NAME, null, selection, selectionArgs,
                null, null, null)) {
            if (c.moveToLast()) {
                coursework = new Coursework(
                        c.getInt(c.getColumnIndex(CourseworkTable.COLUMN_ID)),
                        c.getInt(c.getColumnIndex(CourseworkTable.COLUMN_MODULE_ID)),
                        c.getString(c.getColumnIndex(CourseworkTable.COLUMN_TITLE)),
                        c.getString(c.getColumnIndex(CourseworkTable.COLUMN_DESCRIPTION)),
                        c.getString(c.getColumnIndex(CourseworkTable.COLUMN_PRIORITY)),
                        LocalDate.parse(c.getString(c.getColumnIndex(CourseworkTable.COLUMN_DEADLINE))),
                        LocalTime.parse(c.getString(c.getColumnIndex(CourseworkTable.COLUMN_DEADLINE_TIME)))

                );
                coursework.setImage(c.getBlob(c.getColumnIndex(CourseworkTable.COLUMN_IMAGE)));
                coursework.setCompleted(Coursework.isCompleted(c.getString(c.getColumnIndex(CourseworkTable.COLUMN_COMPLETED))));

            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return null;
        }
        return coursework;
    }


    @SuppressLint("Range")
    public Classes getSelectedClass(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = ClassTable.COLUMN_ID + " =?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor c = db.query(ClassTable.TABLE_NAME, null, selection, selectionArgs,
                null, null, null)) {
            if (c.moveToLast()) {
                return new Classes(
                        c.getInt(c.getColumnIndex(ClassTable.COLUMN_ID)),
                        c.getInt(c.getColumnIndex(ClassTable.COLUMN_MODULE_ID)),
                        c.getInt(c.getColumnIndex(ClassTable.COLUMN_SEMESTER_ID)),
                        c.getInt(c.getColumnIndex(ClassTable.COLUMN_DOW)),
                        LocalTime.parse(c.getString(c.getColumnIndex(ClassTable.COLUMN_START_TIME))),
                        LocalTime.parse(c.getString(c.getColumnIndex(ClassTable.COLUMN_END_TIME))),
                        c.getString(c.getColumnIndex(ClassTable.COLUMN_ROOM)),
                        c.getString(c.getColumnIndex(ClassTable.COLUMN_TYPE))

                );
            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return null;
        }
        return null;
    }


    private boolean isCursorEmpty(Cursor cursor) {
        return cursor.getCount() == 0;
    }

    @SuppressLint("Range")
    public List<Semester> getSemester() {
        List<Semester> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selection = SemesterTable.COLUMN_STUDENT_ID + " = ?";

        try (Cursor c = db.query(
                SemesterTable.TABLE_NAME,
                null,
                selection,
                getStudentIDArray(),
                null,
                null,
                null
        )) {
            if (!isCursorEmpty(c)) {
                while (c.moveToNext()) {
                    list.add(new Semester(
                            c.getInt(c.getColumnIndex(SemesterTable.COLUMN_ID)),
                            c.getString(c.getColumnIndex(SemesterTable.COLUMN_NAME)),
                            LocalDate.parse(c.getString(c.getColumnIndex(SemesterTable.COLUMN_START_DATE))),
                            LocalDate.parse(c.getString(c.getColumnIndex(SemesterTable.COLUMN_END_DATE)))
                    ));
                }

            }

        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));

        }
        return list;
    }

    @SuppressLint("Range")
    public Module getSelectedModule(int id) {
        Module module = null;
        SQLiteDatabase db = getReadableDatabase();
        String selection = ModuleTable.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        String[] columns = {ModuleTable.COLUMN_MODULE_C0DE, ModuleTable.COLUMN_MODULE_NAME};
        try (Cursor c = db.query(ModuleTable.TABLE_NAME, columns, selection, selectionArgs,
                null, null, null)) {
            if (c.moveToLast()) {
                module = new Module(
                        c.getString(c.getColumnIndex(ModuleTable.COLUMN_MODULE_C0DE)),
                        c.getString(c.getColumnIndex(ModuleTable.COLUMN_MODULE_NAME)));
            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return null;

        }
        return module;
    }


    @SuppressLint("Range")
    public List<Integer> getModuleTeachersFiltered(String module) {
        List<Integer> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        final String SQL = """
                SELECT
                  DISTINCT mt.module_id,
                  m.module_code || ' ' || m.module_name AS details
                FROM
                  module_teacher mt
                  JOIN modules m ON m.module_id = mt.module_id
                WHERE
                  student_id = ?
                  AND details LIKE ?
                 """;

        try (Cursor c = db.rawQuery(SQL, new String[]{
                String.valueOf(getStudentID()),
                MessageFormat.format("%{0}%", module)
        })) {

            while (c.moveToNext()) {
                int moduleID = c.getInt(c.getColumnIndex(ModuleTeacherTable.COLUMN_MODULE_ID));
                list.add(moduleID);

            }

        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
        }
        return list;
    }


    @SuppressLint("Range")
    public Teacher getSelectedTeacher(int id) {
        Teacher teacher = null;
        SQLiteDatabase db = getReadableDatabase();
        String selection = TeacherTable.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor c = db.query(TeacherTable.TABLE_NAME, null, selection, selectionArgs,
                null, null, null)) {
            if (c.moveToLast()) {
                teacher = new Teacher(
                        c.getInt(c.getColumnIndex(TeacherTable.COLUMN_ID)),
                        c.getString(c.getColumnIndex(TeacherTable.COLUMN_FIRSTNAME)),
                        c.getString(c.getColumnIndex(TeacherTable.COLUMN_LASTNAME)),
                        c.getString(c.getColumnIndex(TeacherTable.COLUMN_EMAIL)));
            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return null;

        }
        return teacher;
    }


    @SuppressLint("Range")
    public Semester getSelectedSemester(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = SemesterTable.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor c = db.query(SemesterTable.TABLE_NAME, null, selection, selectionArgs,
                null, null, null)) {
            if (c.moveToLast()) {
                return new Semester(
                        c.getInt(c.getColumnIndex(SemesterTable.COLUMN_ID)),
                        c.getString(c.getColumnIndex(SemesterTable.COLUMN_NAME)),
                        LocalDate.parse(c.getString(c.getColumnIndex(SemesterTable.COLUMN_START_DATE))),
                        LocalDate.parse(c.getString(c.getColumnIndex(SemesterTable.COLUMN_END_DATE)))
                );
            }
        } catch (Exception e) {
            Log.d(ERROR_TAG, getErrorMessage(e));
            return null;

        }
        return null;
    }

    public boolean updateModule(Module module) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = Module.contentValues(module);
        long result = db.update(ModuleTable.TABLE_NAME, cv, ModuleTable.COLUMN_ID + "=?", new String[]{String.valueOf(module.getModuleID())});
        return result != -1;
    }


    public boolean updateTeacher(Teacher teacher) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = Teacher.contentValues(teacher);
        long result = db.update(TeacherTable.TABLE_NAME, cv, TeacherTable.COLUMN_ID + "=?", new String[]{String.valueOf(teacher.getUserID())});
        return result != -1;


    }

    public boolean updateSemester(Semester semester) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = Semester.contentValues(semester);
        long result = db.update(SemesterTable.TABLE_NAME, cv, SemesterTable.COLUMN_ID + "=?", new String[]{String.valueOf(semester.semesterID())});
        return result != -1;
    }


    public boolean updateCoursework(Coursework coursework, boolean deleteImage) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = Coursework.contentValues(coursework);
        cv.put(CourseworkTable.COLUMN_COMPLETED, coursework.isCompleted() ? "Yes" : "No");
        ContentValues values = deleteImage(cv, deleteImage, coursework.getImage());
        long result = db.update(CourseworkTable.TABLE_NAME, values, CourseworkTable.COLUMN_ID + "=?", new String[]{String.valueOf(coursework.getCourseworkID())});
        return result != -1;
    }

    private ContentValues deleteImage(ContentValues cv, boolean deleteImage, Bitmap image) {
        if (deleteImage) {
            cv.putNull(CourseworkTable.COLUMN_IMAGE);
            return cv;
        }
        if (image != null) {
            cv.put(CourseworkTable.COLUMN_IMAGE, ImageHandler.getBitmapAsByteArray(image));
        }
        return cv;

    }

    public boolean updateClass(Classes classes) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = Classes.contentValues(classes);
        long result = db.update(ClassTable.TABLE_NAME, cv, ClassTable.COLUMN_ID + "=?", new String[]{String.valueOf(classes.getClassID())});
        return result != -1;
    }

    public boolean deleteRecord(String table, String idField, int id) {
        String whereClause = idField + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        long result = db.delete(table, whereClause, whereArgs);
        return result != -1;
    }

    public int getCourseworkCountByDate(LocalDate deadlineDate) {

        SQLiteDatabase db = getReadableDatabase();
        final String SQL = """
                SELECT
                  COUNT (deadline)
                FROM
                  coursework c
                JOIN modules m ON m.module_id = c.module_id
                                           
                WHERE
                  student_id = ?
                AND deadline = ?
                 """;

        try (Cursor c = db.rawQuery(
                SQL,
                new String[]{
                        String.valueOf(getStudentID()),
                        deadlineDate.toString()
                })) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                return c.getInt(0);
            }
        } catch (Exception e) {

            Log.d(ERROR_TAG, getErrorMessage(e));

        }
        return 0;
    }
}