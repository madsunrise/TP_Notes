package com.rv150.notes.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.rv150.notes.Constants;
import com.rv150.notes.Database.DAO.CategoryDAO;
import com.rv150.notes.Database.DAO.NoteDAO;
import com.rv150.notes.ItemClickSupport;
import com.rv150.notes.Models.Category;
import com.rv150.notes.Models.Note;
import com.rv150.notes.R;
import com.rv150.notes.RecyclerAdapter;
import com.rv150.notes.ThemeChanger;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.rv150.notes.Constants.RC_ADDING_NOTE;
import static com.rv150.notes.Constants.RC_VIEWING_NOTE;
import static com.rv150.notes.Constants.RESULT_MODIFIED;
import static com.rv150.notes.Constants.RESULT_REMOVED;



public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private Drawer drawer;
    private Toolbar toolbar;
    private TextView isEmpty; // Надпись "Нет заметок" при отсутствии записей

    private RecyclerAdapter mRecyclerAdapter;
    private List<Note> mAllNotes;

    private NoteDAO mNoteDAO;
    private CategoryDAO mCategoryDAO;

    private SharedPreferences mSharedPreferences;

    private int mTheme;
    private int choosenCategoryColor;

    private final AtomicLong ID_GENERATOR = new AtomicLong();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeChanger.onActivityCreateSetTheme(this);    // Установка текущей темы при создании активити
        mTheme = ThemeChanger.getTheme();
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditingActivity.class);
                startActivityForResult(intent, RC_ADDING_NOTE);
            }
        });

        mNoteDAO = new NoteDAO(getApplicationContext());
        mCategoryDAO = new CategoryDAO(getApplicationContext());

        setUpDrawer();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_main);
        setUpRecyclerView();

        mAllNotes = mNoteDAO.getAll();      // показываем все заметки при запуске
        mRecyclerAdapter = new RecyclerAdapter(mAllNotes, getApplicationContext());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        isEmpty = (TextView) findViewById(R.id.empty_view);
        if (mAllNotes.isEmpty()) {
            isEmpty.setVisibility(View.VISIBLE);
        }
        else {
            isEmpty.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        int currentTheme = ThemeChanger.getTheme();
        if (mTheme != currentTheme) {
            recreate();        // Пересоздать активити на случай смены темы в настройках
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_ADDING_NOTE && resultCode == RESULT_OK) { // добавили новую заметку
            Bundle extras = data.getExtras();
            Note note = extras.getParcelable(Note.class.getSimpleName());
            if (note != null) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.note_was_created, Toast.LENGTH_SHORT);
                toast.show();
            }
            long idAllNotes = mSharedPreferences.getLong(Constants.ID_ALL_NOTES, -1);
            drawer.setSelection(idAllNotes);    // Выставим пункт "все заметки"
        }

        if (requestCode == RC_VIEWING_NOTE) {
            if (resultCode == RESULT_MODIFIED) { // изменили существующую
                Bundle extras = data.getExtras();
                Note note = extras.getParcelable(Note.class.getSimpleName());
                if (note != null) {
                    mRecyclerAdapter.updateItem(note);
                }
                long idAllNotes = mSharedPreferences.getLong(Constants.ID_ALL_NOTES, -1);
                drawer.setSelection(idAllNotes);    // Выставим пункт "все заметки"
            }
            else if (resultCode == RESULT_REMOVED) {    // удалили заметку
                Bundle extras = data.getExtras();
                Note note = extras.getParcelable(Note.class.getSimpleName());
                if (note != null) {
                    mRecyclerAdapter.removeItem(note);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.note_was_removed, Toast.LENGTH_SHORT);
                    toast.show();
                }
                long idAllNotes = mSharedPreferences.getLong(Constants.ID_ALL_NOTES, -1);
                drawer.setSelection(idAllNotes);    // Выставим пункт "все заметки"
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Long currentSelection = drawer.getCurrentSelection();
        outState.putLong("currentSelection", currentSelection);
    }
                    // Сохранение выбранной категории при повороте экрана
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Long currentSelection = savedInstanceState.getLong("currentSelection");
        drawer.setSelection(currentSelection);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clear_category) { // Удалить все заметки с данной категорией
            new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.want_to_clear_category)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clearCategory();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
            return true;
        }

        if (id == R.id.action_change_category) {  // Изменить категорию
            showEditCategoryDialog(true);
            return true;
        }

        if (id == R.id.action_remove_category) {  // Удалить категорию
            new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.want_to_remove_category)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeCategory();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Создание или изменение уже существующей категории (в зависимости от флага)
    private void showEditCategoryDialog(final boolean updateExisting) {
        choosenCategoryColor = -1; // Ставим белый цвет как цвет по умолчанию для категории
        final View dialogView = View.inflate(this, R.layout.editing_category_dialog, null);

        new AlertDialog.Builder(this)
        .setTitle(updateExisting? R.string.updating_category : R.string.creating_category)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText inputName = (EditText) dialogView.findViewById(R.id.input_category_name);
                String result = inputName.getText().toString();
                if (result.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.field_is_empty, Toast.LENGTH_SHORT);
                    toast.show();
                    showEditCategoryDialog(updateExisting); // Вызываем диалог заново
                }
                else {
                    result = result.substring(0, 1).toUpperCase() + result.substring(1);
                    if (updateExisting) {
                        updateCategory(result);
                    }
                    else {
                        createCategory(result);
                    }
                }
            }
        })
        .setNegativeButton(R.string.cancel, null)
        .setView(dialogView)
        .show();
    }


    // Создание и сохранение новой категории
    private void createCategory(String name) {
        Category category = new Category(name);
        category.setColor(choosenCategoryColor);
        long id;
        try {
            id = mCategoryDAO.insertCategory(category);
        }
        catch (SQLiteConstraintException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.category_with_this_name_already_exists, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        category.setId(id);

        addCategoryToDrawer(category);

        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.category_was_created, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void updateCategory (String result) {
        long drawerItemId = drawer.getCurrentSelection();
        PrimaryDrawerItem drawerItem = (PrimaryDrawerItem) drawer.getDrawerItem(drawerItemId);
        Category category = (Category) drawerItem.getTag();
        category.setName(result);
        category.setColor(choosenCategoryColor);
        try {
            mCategoryDAO.updateCategory(category);
        }
        catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.category_with_this_name_already_exists, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        drawerItem.withName(category.getName());

        Drawable original = ContextCompat.getDrawable(this, R.drawable.circle);
        Drawable.ConstantState constantState = original.getConstantState();
        if (constantState == null) {
            Log.wtf(TAG, "Drawable.ConstantState is null");
            return;
        }
        Drawable icon = original.getConstantState().newDrawable();
        icon.mutate();
        icon.setColorFilter(category.getColor(), PorterDuff.Mode.MULTIPLY);

        drawerItem.withIcon(icon);
        drawer.updateItem(drawerItem);
        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.category_was_updated, Toast.LENGTH_SHORT);
        toast.show();
    }

    // Удалить заметки с данной категорией
    private void clearCategory() {
        long idAllNotes = mSharedPreferences.getLong(Constants.ID_ALL_NOTES, -1);
        long currentSelection = drawer.getCurrentSelection();
        if (idAllNotes == currentSelection) { // Выбран пункт "Все заметки"
            mNoteDAO.removeAll();
            mRecyclerAdapter.removeAllItems();
        }
        else {              // Получаем выбранный объект в Drawer, а из него - объект категории
            IDrawerItem drawerItem = drawer.getDrawerItem(currentSelection);
            Category category = (Category) drawerItem.getTag();
            mNoteDAO.removeNotesWithCategory(category.getId());
            mRecyclerAdapter.removeAllItems();
        }
        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.category_was_cleared, Toast.LENGTH_SHORT);
        toast.show();
        isEmpty.setVisibility(View.VISIBLE);
    }

    // Удалить саму категорию
    private void removeCategory() {
        long idAllNotes = mSharedPreferences.getLong(Constants.ID_ALL_NOTES, -1);
        long currentSelection = drawer.getCurrentSelection();
        if (idAllNotes == currentSelection) {                   // Выбран пункт "Все заметки"
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.this_category_cant_be_removed, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        IDrawerItem drawerItem = drawer.getDrawerItem(currentSelection);
        Category category = (Category) drawerItem.getTag();
        mCategoryDAO.removeCategory(category.getId());
        drawer.removeItem(currentSelection);
        drawer.setSelection(idAllNotes);
        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.category_was_removed, Toast.LENGTH_SHORT);
        toast.show();
    }



    public void openColorPicker(View view) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.choose_color)
                .initialColor(choosenCategoryColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        choosenCategoryColor = selectedColor;
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }



    private void setUpDrawer() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        long idAllNotes = ID_GENERATOR.getAndIncrement();
        editor.putLong(Constants.ID_ALL_NOTES, idAllNotes);
        PrimaryDrawerItem allNotes = new PrimaryDrawerItem()
                .withIdentifier(idAllNotes)
                .withName(R.string.all_notes);

        long idCreateCategory = ID_GENERATOR.getAndIncrement();
        editor.putLong(Constants.ID_CREATE_CATEGORY, idCreateCategory);
        SecondaryDrawerItem createCategory = new SecondaryDrawerItem()
                .withIdentifier(idCreateCategory)
                .withName(R.string.create_category)
                .withSelectable(false);


        long idSettings = ID_GENERATOR.getAndIncrement();
        editor.putLong(Constants.ID_SETTINGS, idSettings);
        PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withIdentifier(idSettings)
                .withName(R.string.settings)
                .withSelectable(false);

        long idAbout = ID_GENERATOR.getAndIncrement();
        editor.putLong(Constants.ID_ABOUT, idAbout);
        PrimaryDrawerItem about = new PrimaryDrawerItem()
                .withIdentifier(idAbout)
                .withName(R.string.about)
                .withSelectable(false);

        editor.apply();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.nav_header)
                .addDrawerItems(
                        allNotes,
                        createCategory,
                        new DividerDrawerItem(),
                        settings,
                        about
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        drawerPushed(drawerItem);
                        return true;
                    }
                })
                .build();

        // Добавление существующих категорий
        List<Category> categories = mCategoryDAO.getAll();
        for (Category category: categories) {
            addCategoryToDrawer(category);
        }
    }

    private void addCategoryToDrawer(Category category) {
        long id = ID_GENERATOR.getAndIncrement();

        Drawable original = ContextCompat.getDrawable(this, R.drawable.circle);
        Drawable.ConstantState constantState = original.getConstantState();
        if (constantState == null) {
            Log.wtf(TAG, "Drawable.ConstantState is null");
            return;
        }
        Drawable icon = original.getConstantState().newDrawable();
        icon.mutate();
        icon.setColorFilter(category.getColor(), PorterDuff.Mode.MULTIPLY);

        PrimaryDrawerItem newItem =  new PrimaryDrawerItem()
                .withIdentifier(id)
                .withName(category.getName())
                .withTag(category)     // в поле Tag сохраним ссылку на связанную категорию
                .withIcon(icon);


        long idCreateCategory = mSharedPreferences.getLong(Constants.ID_CREATE_CATEGORY, 0);
        int position = drawer.getPosition(idCreateCategory); // получаем позицию элемента "Создать категорию"
        drawer.addItemAtPosition(newItem, position);    //  и помещаем новую категорию на эту позицию
        drawer.closeDrawer();
    }


    // Обработка нажатий на элемент nav. drawer
    private void drawerPushed(IDrawerItem drawerItem) {
        long itemId = drawerItem.getIdentifier();
        if (itemId == mSharedPreferences.getLong(Constants.ID_CREATE_CATEGORY, -1)) {
                showEditCategoryDialog(false);
        }
        else if (itemId == mSharedPreferences.getLong(Constants.ID_ALL_NOTES, -1)) {
            setTitle(getString(R.string.all_notes));
            mAllNotes = mNoteDAO.getAll();
            mRecyclerAdapter.setItems(mAllNotes);
            if (!mAllNotes.isEmpty()) {
                isEmpty.setVisibility(View.GONE);
            }
        }
        else if (itemId == mSharedPreferences.getLong(Constants.ID_SETTINGS, -1)) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (itemId == mSharedPreferences.getLong(Constants.ID_ABOUT, -1)) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        else {      // Фильтруем заметки по определенной категории
            Category category = (Category) drawerItem.getTag();
            final List<Note> filtered = mNoteDAO.getFromCategory(category.getId());
            mRecyclerAdapter.setItems(filtered);
            setTitle(category.getName());
            if (filtered.isEmpty()) {
                isEmpty.setVisibility(View.VISIBLE);
            }
        }
        drawer.closeDrawer();
    }





    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        setUpItemTouchHelper();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    mFab.hide();
                } else
                    mFab.show();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int pos, View v) {
                Note note = mRecyclerAdapter.getItemAtPosition(pos);
                Intent intent = new Intent(getApplicationContext(), ViewingActivity.class);
                intent.putExtra(Note.class.getSimpleName(), note);
                startActivityForResult(intent, RC_VIEWING_NOTE);
            }
        });
    }




    // Реализует возможность удаления заметки свайпом
    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    Drawable background;
                    Drawable xMark;
                    int xMarkMargin;
                    boolean initiated;

                    private void init() {
                        background = new ColorDrawable(Color.RED);
                        xMark = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_clear_24dp);
                        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        xMarkMargin = (int) MainActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
                        initiated = true;
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        final int pos = viewHolder.getAdapterPosition();
                        final Note note = mRecyclerAdapter.getItemAtPosition(pos);
                        // Удаляем из RecyclerView
                        mRecyclerAdapter.removeItemAtPosition(pos);
                        // И из базы
                        mNoteDAO.removeNote(note.getId());
                        if (mRecyclerAdapter.getItemCount() == 0) {
                            isEmpty.setVisibility(View.VISIBLE);
                        }

                        View parentLayout = findViewById(R.id.recycler_view_main);
                        Snackbar snackbar = Snackbar
                                .make(parentLayout, R.string.note_has_been_deleted, Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Откат удаления
                                        long id = mNoteDAO.insertNote(note);
                                        note.setId(id);
                                        mRecyclerAdapter.addItem(note, pos);
                                        isEmpty.setVisibility(View.GONE);
                                    }
                                });

                        snackbar.show();
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        View itemView = viewHolder.itemView;
                        if (viewHolder.getAdapterPosition() == -1) {
                            return;
                        }
                        if (!initiated) {
                            init();
                        }
                        // draw red background
                        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        background.draw(c);

                        // draw x mark
                        int itemHeight = itemView.getBottom() - itemView.getTop();
                        int intrinsicWidth = xMark.getIntrinsicWidth();
                        int intrinsicHeight = xMark.getIntrinsicWidth();
                        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                        int xMarkRight = itemView.getRight() - xMarkMargin;
                        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                        int xMarkBottom = xMarkTop + intrinsicHeight;
                        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                        xMark.draw(c);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }

                };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
