package cascading.mongodb;

import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.TapException;
import cascading.tap.hadoop.TapCollector;
import cascading.tap.hadoop.TapIterator;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntryIterator;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * Date: May 21, 2010
 * Time: 8:23:51 PM
 */
public class MongoDBTap extends Tap {

    static final Logger log = Logger.getLogger(MongoDBTap.class.getName());

    private static final String SCHEME = "mongodb:/";

    private transient Mongo m;
    private transient DB db;

    private String collection;
    private String database;
    private String hostname;
    private int port = 27017;
    private String username;
    private String password;
    private String connectionUrl;

    private CollectionDescriptor collectionDescriptor;

    public MongoDBTap(String hostname, String database, String collection, MongoDBScheme scheme, CollectionDescriptor descriptor)
    {
        this(database, collection, scheme);
        this.hostname = hostname;
        this.collectionDescriptor = descriptor;
        this.connectionUrl = hostname + "/" + database + "/" + collection;
    }

    public MongoDBTap(String database, String collection, MongoDBScheme scheme) {
        super(scheme, SinkMode.KEEP);
        this.collection = collection;
        this.database = database;
    }

//    public MongoDBTap(Mongo mongo, String database, String collection, MongoDBScheme scheme) {
//        this(database, collection, scheme);
//        this.m = mongo;
//    }
//
//    public MongoDBTap(String hostname, String database, String collection, MongoDBScheme scheme) {
//        this(database, collection, scheme);
//        this.hostname = hostname;
//    }
//
//    public MongoDBTap(String hostname, int port, String database, String collection, MongoDBScheme scheme) {
//        this(hostname, database, collection, scheme);
//        this.port = port;
//    }

    private URI getURI() {
        try {
            return new URI(SCHEME, collection, null);
        }
        catch (URISyntaxException e) {
            throw new TapException("unable to create Uri", e);
        }
    }

    private Mongo getMongo() throws UnknownHostException {
        if (m == null) {
            m = new Mongo(hostname, port);
        }

        return m;
    }

    private DB getDB() {

        if (m == null)
        {
            try
            {
                m = new Mongo(hostname, port);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


        if (db == null) {
            db = m.getDB(database);
        }

        return db;
    }

    public boolean isSink()
    {
        return collectionDescriptor != null;
    }

    public boolean isWriteDirect()
    {
        return true;
    }

    @Override
    public Path getPath() {
        return new Path(SCHEME + connectionUrl.replaceAll( ":", "_" ));
    }

    @Override
    public TupleEntryIterator openForRead(JobConf jobConf) throws IOException {
        return new TupleEntryIterator(getSourceFields(), new TapIterator(this, jobConf));
    }

    @Override
    public TupleEntryCollector openForWrite(JobConf jobConf) throws IOException {

        if (!isSink())
            throw new TapException("This tap cannot be used as a sink.");

        return new TapCollector(this, jobConf);
    }

    @Override
    public boolean makeDirs(JobConf jobConf) throws IOException {

        if (getDB().collectionExists(collection))
            return true;

        log.debug("Creating collection: {name = " + collection + "};");
        DBCollection coll = getDB().getCollection(collection);


        if (coll != null)
            return true;

        return false;

    }

    @Override
    public boolean deletePath(JobConf jobConf) throws IOException {

        if (!getDB().collectionExists(collection))
            return true;

        log.debug("Removing collection: {name = " + collection + "};");
        getDB().getCollection(collection).drop();

        return true;

    }

    @Override
    public boolean pathExists(JobConf jobConf) throws IOException {

        if (!isSink())
            return true;

        return getDB().collectionExists(collection);
    }

    @Override
    public long getPathModified(JobConf jobConf) throws IOException {
        return System.currentTimeMillis();
    }

    @Override
    public void sinkInit(JobConf jobConf) throws IOException {

        if (!isSink())
            return;

        log.debug("Sinking to collection: {name=" + collection + "};");

        if (isReplace() && jobConf.get("mapred.task.partition") == null) {
            deletePath(jobConf);
        }

        makeDirs(jobConf);
        super.sinkInit(jobConf);

    }

    @Override
    public String toString()
    {
        return "MongoDBTap {host=" + hostname + ", port=" + port + ", collection=" + collection + "}";
    }


    public String getCollection() {
        return collection;
    }
}