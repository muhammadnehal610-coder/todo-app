package todo.com.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoDBManager {
    private static MongoDBManager instance;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> tasksCollection;
    private String connectionError;

    private static final String CONNECTION_STRING = "mongodb://muhammadnehal610:dbmongo@mydb-shard-00-00.opigg.mongodb.net:27017,mydb-shard-00-01.opigg.mongodb.net:27017,mydb-shard-00-02.opigg.mongodb.net:27017/?ssl=true&replicaSet=atlas-h86jjk-shard-0&authSource=admin&appName=myDB";    private static final String DATABASE_NAME = "todoapp";
    private static final String COLLECTION_NAME = "tasks";

    private MongoDBManager() {
        try {
            connectToDatabase();
        } catch (Exception e) {
            connectionError = rootMessage(e);
            System.err.println("Failed to connect to MongoDB: " + connectionError);
            e.printStackTrace();
        }
    }

    public static synchronized MongoDBManager getInstance() {
        if (instance == null) {
            instance = new MongoDBManager();
        }
        return instance;
    }

    private void connectToDatabase() {
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DATABASE_NAME);
            tasksCollection = database.getCollection(COLLECTION_NAME);

            database.runCommand(new Document("ping", 1));
            connectionError = null;
            System.out.println("Connected to MongoDB successfully.");
            System.out.println("Database: " + DATABASE_NAME);
            System.out.println("Collection: " + COLLECTION_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to MongoDB", e);
        }
    }

    public String addTask(String title, String category, String priority, String dueDate, String description) {
        try {
            Document task = new Document()
                    .append("title", title)
                    .append("category", category)
                    .append("priority", priority)
                    .append("dueDate", dueDate)
                    .append("description", description)
                    .append("status", "Pending")
                    .append("createdAt", new Date())
                    .append("updatedAt", new Date());

            tasksCollection.insertOne(task);
            ObjectId id = task.getObjectId("_id");
            System.out.println("Task added with ID: " + id);
            return id.toString();
        } catch (Exception e) {
            System.err.println("Error adding task: " + e.getMessage());
            return null;
        }
    }

    public List<Document> getAllTasks() {
        try {
            List<Document> tasks = new ArrayList<>();
            tasksCollection.find().into(tasks);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Document getTaskById(String id) {
        try {
            return tasksCollection.find(Filters.eq("_id", new ObjectId(id))).first();
        } catch (Exception e) {
            System.err.println("Error fetching task by ID: " + e.getMessage());
            return null;
        }
    }

    public boolean updateTask(String id, String title, String category, String priority, String dueDate, String description, String status) {
        try {
            Document update = new Document()
                    .append("title", title)
                    .append("category", category)
                    .append("priority", priority)
                    .append("dueDate", dueDate)
                    .append("description", description)
                    .append("status", status)
                    .append("updatedAt", new Date());

            var result = tasksCollection.updateOne(
                    Filters.eq("_id", new ObjectId(id)),
                    new Document("$set", update)
            );

            System.out.println("Task updated: " + id);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Error updating task: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTask(String id) {
        try {
            var result = tasksCollection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            System.out.println("Task deleted: " + id);
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting task: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTaskStatus(String id, String status) {
        try {
            var result = tasksCollection.updateOne(
                    Filters.eq("_id", new ObjectId(id)),
                    new Document("$set", new Document()
                            .append("status", status)
                            .append("updatedAt", new Date()))
            );

            System.out.println("Task status updated to: " + status);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Error updating task status: " + e.getMessage());
            return false;
        }
    }

    public List<Document> getTasksByPriority(String priority) {
        try {
            List<Document> tasks = new ArrayList<>();
            tasksCollection.find(Filters.eq("priority", priority)).into(tasks);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error filtering by priority: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Document> getTasksByStatus(String status) {
        try {
            List<Document> tasks = new ArrayList<>();
            tasksCollection.find(Filters.eq("status", status)).into(tasks);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error filtering by status: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Document> getTasksByCategory(String category) {
        try {
            List<Document> tasks = new ArrayList<>();
            tasksCollection.find(Filters.eq("category", category)).into(tasks);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error filtering by category: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Document> getTasksByDate(String date) {
        try {
            List<Document> tasks = new ArrayList<>();
            tasksCollection.find(Filters.eq("dueDate", date)).into(tasks);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error filtering by date: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void closeConnection() {
        try {
            if (mongoClient != null) {
                mongoClient.close();
                System.out.println("MongoDB connection closed.");
            }
        } catch (Exception e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public long countTasks() {
        try {
            return tasksCollection.countDocuments();
        } catch (Exception e) {
            System.err.println("Error counting tasks: " + e.getMessage());
            return 0;
        }
    }

    public MongoCollection<Document> getTasksCollection() {
        return tasksCollection;
    }

    public boolean isConnected() {
        try {
            if (database == null) {
                return false;
            }
            database.runCommand(new Document("ping", 1));
            connectionError = null;
            return true;
        } catch (Exception e) {
            connectionError = rootMessage(e);
            System.err.println("Connection check failed: " + connectionError);
            return false;
        }
    }

    public String getConnectionError() {
        return connectionError;
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() != null ? current.getMessage() : throwable.toString();
    }
}
