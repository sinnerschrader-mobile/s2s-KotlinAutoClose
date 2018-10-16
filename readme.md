Kotlin AutoClose
============

The project is forked from Kyle Wood's (DemonWav) [kotlin-utils](https://discuss.kotlinlang.org/t/kotlin-needs-try-with-resources/214).

While it is a clean room implementation, project structure, UnitTest and even the readme.md are simply adapted from Kyle's work.

Thanks Kyle!

`manageRessources {}`
----------

Kotlin has no answer to Java's try-with-resources mechanic. This is sad as it's probably the only thing Java has that Kotlin doesn't.
Kotlin's response is with the `AutoCloseable#use()` extension method. This is fine except for three very big and annoying issues:

1. No scoping help. In Java with `try-with-resources`, when you are finished using the given resource they would not only automatically be
   closed for you, they would also conveniently go out of scope.
2. No support for multiple resources. The only way to use multiple resources is to nest `use()` blocks.
3. No support for catching exceptions. In Java with `try-with-resources` you can catch exceptions thrown in the resource initialization in
   the same try block. In Kotlin, you have to wrap the `use()` block in another `try-catch` block.

There is an approach by Kyle Wood (DemonWav) which approaches all of these issues with Kotlin extension functions:
https://github.com/DemonWav/kotlin-utils

s2s-KotlinAutoClose was developed without knowledge of Kyle's project. But after proposing my approach in Kotlin Slack, Kotlin Utils was pointed out to me and I onboarded myself onto [this discussion](https://discuss.kotlinlang.org/t/kotlin-needs-try-with-resources/214) around it:.

I realized, that my solution is a lot more standard and less complex.
It uses Kotlin's regular try/catch mechanisms and therefore does not require a final `finally` (see kotlin-utils [readme.md](https://github.com/DemonWav/kotlin-utils/blob/master/readme.md))

On the other hand it does not solve issue #3. You still need a separate try/catch around your manageRessources block if you want to handle exceptions!

It basically follows closely Orangy's [suggestion](https://discuss.kotlinlang.org/t/kotlin-needs-try-with-resources/214/2).

Here's an example usage of autoClose.

```kotlin
class Example {
    fun example() {
        try {
            manageRessources {
                val connection = DriverManager.getConnection("MyDriver").autoClose()
                val statement = connection.prepareStatement("SELECT ?").autoClose()
                // This means you can add resources to the manager at any time, which is a bonus
                statement.setInt(1, 1)
                val rs = statement.executeQuery().autoClose()

                // Do database stuff
            }
        } catch (e: IOException) {
            // This does support multiple catch blocks
            LOGGER.error("IO error", e)
        } catch (e: SQLException) {
            LOGGER.error("Error in query", e)
        } finally {
            // Not required
        }
    }
}
```

And the Java equivalent of the bytecode this generates:

```java
public final class Example {
   public final void example() {
      try {
         String var2;
         try {
            RessourceManager manager$iv = new RessourceManager();
            boolean closed$iv = false;
            boolean var19 = false;

            try {
               var19 = true;
               Connection connection = (Connection)manager$iv.autoClose((AutoCloseable)DriverManager.getConnection("MyDriver"));
               PreparedStatement statement = (PreparedStatement)manager$iv.autoClose((AutoCloseable)connection.prepareStatement("SELECT ?"));
               statement.setInt(1, 1);
               ResultSet var10000 = (ResultSet)manager$iv.autoClose((AutoCloseable)statement.executeQuery());
               Unit var3 = Unit.INSTANCE;
               var19 = false;
            } catch (Throwable var20) {
               closed$iv = true;
               manager$iv.closeAndRethrow(var20);
               throw null;
            } finally {
               if (var19) {
                  if (!closed$iv) {
                     manager$iv.close();
                  }

               }
            }

            manager$iv.close();
         } catch (IOException var22) {
            LOGGER.error("IO error", var22);
         } catch (SQLException var23) {
            LOGGER.error("Error in query", var23);
         }

      } finally {
         ;
      }
   }
}

// For reference:
public final class RessourceManager {
   private final ArrayList closeables;

   public final AutoCloseable autoClose(AutoCloseable $receiver) {
      if ($receiver != null) {
         this.closeables.add($receiver);
      }

      return $receiver;
   }


    // Other stuff omitted
}
```

For reference, this is the standard Kotlin equivalent:

```kotlin
class Example {
    fun example() {
        try {
            DriverManager.getConnection("MyDriver").use { connection ->
                connection.prepareStatement("SELECT ?").use { statement ->
                    statement.setInt(1, 1)
                    statement.executeQuery().use { rs ->
                        // Do database stuff
                    }
                }
            }
        } catch (e: IOException) {
            LOGGER.error("IO error", e)
        } catch (e: SQLException) {
            LOGGER.error("Error in query", e)
        }
    }
}
```

And this is the Java equivalent:

```java
public class Example {
    public void example() {
        try (
            final Connection connection = DriverManager.getConnection("MyDriver");
            final PreparedStatement statement = connection.prepareStatement("SELECT ?")
        ) {
            statement.setInt(1, 1);
            try (final ResultSet rs = statement.executeQuery()) {
                // Do database stuff
            }
        } catch (IOException e) {
            LOGGER.error("IO error", e);
        } catch (SQLException e) {
            LOGGER.error("Error in query", e);
        }
    }
}
```

