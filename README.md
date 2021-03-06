# Hadi (English Version)

**Hadi** is an Android ORM Framework. It makes SQLite using in Android easy and simple. 
Hadi is and open source project.

It was originally created by The9tCat [http://sourceforge.net/users/the9tcat](http://sourceforge.net/users/the9tcat)

# How to use

Examples project: [https://github.com/PepeuCps/HadiExamples](https://github.com/PepeuCps/HadiExamples)

1. Import Hadi library **(hadi*.jar)** into your Android project. (See the last build/binary in [https://github.com/PepeuCps/Hadi/releases](https://github.com/PepeuCps/Hadi/releases)).
If you are using Eclipse, add the lib to the build path.

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
		
		/** for update data */
		b1.sn = "sn987654321";
		dao.update_by_primary(b1);
		
		/** for select */
		String[] args = {"0"};
		List<Book> books = (List<Book>)dao.select(Book.class, false, " id > ?", args, null, null, null, null);
		System.out.println(books.size());
		for(Book b:books){
			System.out.println(b.id+"=="+b.name);
		}
		b1.id = 1;
		
		/** for delete */
		dao.delete_by_primary(b1);		

5. You could find all the example codes from demo project or if you have any quaestions send a e-mail to hadiormdev@gmail.com.

# PROGUARD

Pay attention with proguard configurations when you use HADI because the annotations and the property names can cause strange names (table names and columns) in your database.
Just configure proguard-project.txt with this definitions:

1. Don't obuscate HADI:
<code>-keep class com.the9tcat.hadi.** { *; }</code>

2. Don't obfuscate your model classes:
<code>-keep class com.yourpackage.model.** { *; }</code>


# Hadi (Portuguese Version)

**Hadi** é um framework ORM para Android. Ele faz o SQLite ficar fácil de usar. 
O Hadi é um projeto open source. Participe do nosso projeto e contribua.

Este projeto foi criado originalmente por The9tCat [http://sourceforge.net/users/the9tcat](http://sourceforge.net/users/the9tcat)

# Como Usar

Projeto com exemplos: [https://github.com/PepeuCps/HadiExamples](https://github.com/PepeuCps/HadiExamples)

1. Importe a biblioteca do Hadi **(hadi*.jar)** para dentro do seu projeto Android. (Veja a última build em [https://github.com/PepeuCps/Hadi/releases](https://github.com/PepeuCps/Hadi/releases))
Se estiver usando Eclipse não esqueça de clicar com o botão direito na lib e escolher "Add To Build Path".

2. Edite seu **AndroidManifest.xml**

Adicione o atributo "android:name" no seu elemento application. Algo parecido com isso:

		<application android:name="com.the9tcat.hadi.HadiApplication" android:icon="@drawable/icon" android:label="@string/app_name">

Defina o nome da sua base de dados e a versão (dentro da tag  Application). Algo parecido com isso:

		<application android:name="com.the9tcat.hadi.HadiApplication" android:icon="@drawable/icon" android:label="@string/app_name">
  		<meta-data android:name="Hadi_DB_NAME" android:value="demo.db" />
		<meta-data android:name="Hadi_DB_VERSION" android:value="1" />

3. Escreva suas classes de modelo (suas tabelas):

		import com.the9tcat.hadi.annotation.Column;
		import com.the9tcat.hadi.annotation.Table;
		
		@Table(name="MinhaTabela") //define your table's name
		public class Book {
		
			@Column(autoincrement=true)
			public int id;
			
			// define the table's column
			@Column(name="sn")
			public String sn;
			
			@Column(name = "name")
			public String name;
		}
	

4. Use o DefaultDAO para inserir, atualizar, selecionar ou apagar  os dados:

		DefaultDAO dao = new DefaultDAO(this); // "this" é o seu contexto Android, algo como o GetApplicationContext()

		/** para salvar dados na base de dados */
		Book b1 = new Book();
		b1.name = "Who Moved My Cheese";
		b1.sn = "sn123456789";
		dao.insert(b1);
		
		/** para atualizar dados */
		b1.sn = "sn987654321";
		dao.update_by_primary(b1);
		
		/** para selecionar dados */
		String[] args = {"0"};
		List<Book> books = (List<Book>)dao.select(Book.class, false, " id > ?", args, null, null, null, null);
		System.out.println(books.size());
		for(Book b:books){
			System.out.println(b.id+"=="+b.name);
		}
		b1.id = 1;
		
		/** para apagar os dados */
		dao.delete_by_primary(b1);		

5. Você pode achar mais exemplo nos projeto de demonstração ou qualquer dúvida enviar um e-mail para hadiormdev@gmail.com.

# PROGUARD

Preste atenção com as configurações do Proguard quando você usa-lo em conjunto com o HADI. As anotações e os nomes das classes e propriedades podem gerar resultados estranhos na sua base de dados.
Configure o proguard-project.txt com a seguintes definições:

1. Não obfusque o HADI:
<code>-keep class com.the9tcat.hadi.** { *; }</code>

2. Não obfusque suas classes de modelo:
<code>-keep class com.yourpackage.model.** { *; }</code>

