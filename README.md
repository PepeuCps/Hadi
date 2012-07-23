# Hadi

**Hadi** is an Android ORM Framework. It makes SQLite using in Android easy and simple. 
Hadi is and open source project. You can use it free, change it and even re-publish it.

This project was imported from SourceForge. [http://hadi.sourceforge.net/](http://hadi.sourceforge.net/)

It was originally created by The9tCat [http://sourceforge.net/users/the9tcat](http://sourceforge.net/users/the9tcat)

# How to use

1. Import Hadi library **(hadi_sdk.jar)** into your Android project.

2. Edit your **AndroidManifest.xml**

Add attribute "android:name" into element application, it looks like this:

<application android:name="com.the9tcat.hadi.HadiApplication" android:icon="@drawable/icon" android:label="@string/app_name">

Define your database name and version under element Application, it looks like this::

<application android:name="com.the9tcat.hadi.HadiApplication" android:icon="@drawable/icon" android:label="@string/app_name">
  		<meta-data android:name="Hadi_DB_NAME" android:value="demo.db" />
			<meta-data android:name="Hadi_DB_VERSION" android:value="1" />

3. Write your table's model class:

		import com.the9tcat.hadi.annotation.Column;
		import com.the9tcat.hadi.annotation.Table;
		
		@Table(name="Hello") //define your table's name
		public class Book {
		
			@Column(autoincrement=true)
			public int id;
			
			// define the table's column
			@Column(name="sn")
			public String sn;
			
			@Column(name = "")
			public String name;
		}
	

4. Use DefaultDAO to insert, update, select and delete data:

		DefaultDAO dao = new DefaultDAO(this); // "this" is android context
		/** for save data to database */
		Book b1 = new Book();
		b1.name = "Who Moved My Cheese";
		b1.sn = "sn123456789";
		dao.insert(b1);

5. You could find all the example codes from demo project.