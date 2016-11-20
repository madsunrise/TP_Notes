package com.rv150.notes.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.rv150.notes.Database.DAO.CategoryDAO;
import com.rv150.notes.Database.DAO.NoteDAO;
import com.rv150.notes.ItemClickSupport;
import com.rv150.notes.Models.Category;
import com.rv150.notes.Models.Note;
import com.rv150.notes.R;
import com.rv150.notes.RecyclerAdapter;
import com.rv150.notes.Constants;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.rv150.notes.Constants.RC_ADDING_NOTE;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private Drawer drawer;
    private Toolbar toolbar;

    private RecyclerAdapter mRecyclerAdapter;
    private List<Note> mAllNotes;

    private NoteDAO mNoteDAO;
    private CategoryDAO mCategoryDAO;

    private SharedPreferences mSharedPreferences;


    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddingActivity.class);
                startActivityForResult(intent, RC_ADDING_NOTE);
            }
        });

        mNoteDAO = new NoteDAO(getApplicationContext());
        mCategoryDAO = new CategoryDAO(getApplicationContext());


        setUpDrawer();


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_main);
        setUpRecyclerView();


        mAllNotes = mNoteDAO.getAll();
        updateRecyclerWithData(mAllNotes);  // показываем все заметки
    }








    @Override
    protected void onResume() {
        super.onResume();



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_ADDING_NOTE && resultCode == RESULT_OK) {
                mAllNotes = mNoteDAO.getAll();
                updateRecyclerWithData(mAllNotes);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void showCreateCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.creating_category);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input, 70, 0, 100, 0);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String result = input.getText().toString();
                if (result.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.field_is_empty, Toast.LENGTH_SHORT);
                    toast.show();
                    showCreateCategoryDialog(); // Вызываем заново
                }
                else {
                    result = result.substring(0, 1).toUpperCase() + result.substring(1);
                    createCategory(result);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void createCategory(String name) {
        Category category = new Category(name);
        long id;
        try {
            id = mCategoryDAO.insertCategory(category);
        }
        catch (RuntimeException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.category_with_this_name_already_exists, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        category.setId(id);

        addCategoryToDrawer(name);

        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.category_was_created, Toast.LENGTH_SHORT);
        toast.show();
    }



    private void setUpDrawer() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        long idAllNotes = ID_GENERATOR.getAndIncrement();
        editor.putLong(Constants.ID_ALL_NOTES, idAllNotes);
        PrimaryDrawerItem allNotes = new PrimaryDrawerItem()
                .withIdentifier(idAllNotes)
                .withName(R.string.all_notes)
                // withIcon
                ;

        long idCreateCategory = ID_GENERATOR.getAndIncrement();
        editor.putLong(Constants.ID_CREATE_CATEGORY, idCreateCategory);
        SecondaryDrawerItem createCategory = new SecondaryDrawerItem()
                .withIdentifier(idCreateCategory)
                .withName(R.string.create_category)
                // withIcon
                .withSelectable(false);


        long idSettings = ID_GENERATOR.getAndIncrement();
        editor.putLong(Constants.ID_SETTINGS, idSettings);
        PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withIdentifier(idSettings)
                .withName(R.string.settings)
                .withSelectable(false);

        editor.apply();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.nav_header_main)
                .addDrawerItems(
                        allNotes,
                        createCategory,
                        new DividerDrawerItem(),
                        settings
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
            addCategoryToDrawer(category.getName());
        }
    }

    private void addCategoryToDrawer(String name) {
        long id = ID_GENERATOR.getAndIncrement();
        PrimaryDrawerItem newItem =  new PrimaryDrawerItem()
                .withIdentifier(id)
                .withName(name);
        // withIcon

        long idCreateCategory = mSharedPreferences.getLong(Constants.ID_CREATE_CATEGORY, 0);
        int position = drawer.getPosition(idCreateCategory); // получаем позицию "Создать категорию"
        drawer.addItemAtPosition(newItem, position);    //  и помещаем новую категорию на эту позицию
        drawer.closeDrawer();
    }


    private void drawerPushed(IDrawerItem drawerItem) {
        long itemId = drawerItem.getIdentifier();
        if (itemId == Constants.DRAWER_ID_CREATE_CATEGORY) {
                showCreateCategoryDialog();
                return;
        }
        if (itemId == Constants.DRAWER_ID_ALL_NOTES) {
            updateRecyclerWithData(mAllNotes);
        }
        drawer.closeDrawer();
    }




    private void updateRecyclerWithData(List<Note> newData) {
        mRecyclerAdapter = new RecyclerAdapter(newData, getApplicationContext());
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }



    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
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

            }
        });
    }




    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
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

                    // not important, we don't want drag & drop
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
                        //position = viewHolder.getAdapterPosition();
                        //deleteItem();
//                        View parentLayout = findViewById(R.id.mRecyclerView);
//                        Snackbar snackbar = Snackbar
//                                .make(parentLayout, R.string.product_has_been_deleted, Snackbar.LENGTH_LONG)
//                                .setAction(R.string.undo, new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        restoreItem();
//                                    }
//                                });
//
//                        snackbar.show();
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        View itemView = viewHolder.itemView;

                        // not sure why, but this method get's called for viewholder that are already swiped away
                        if (viewHolder.getAdapterPosition() == -1) {
                            // not interested in those
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

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

}
