package cascading.mongodb;

import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.log4j.Logger;

import java.io.IOException;


/**
 * Date: May 21, 2010
 * Time: 8:32:20 PM
 */
public class MongoDBScheme extends Scheme {
    private static final Logger log = Logger.getLogger(MongoDBScheme.class.getName());

    private Class<? extends OutputFormat> outputFormatClass;
    private MongoDocument documentFormat;

    public MongoDBScheme(Class<? extends MongoDBOutputFormat> outputFormatClass) {
        this.outputFormatClass = outputFormatClass;
    }    

    public void sourceInit(Tap tap, JobConf jobConf) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sinkInit(Tap tap, JobConf jobConf) throws IOException {

        String collection = ((MongoDBTap) tap).getCollection();
        String database = ((MongoDBTap) tap).getDatabase();
        documentFormat = ((MongoDBTap) tap).getDocumentFormat();

        jobConf.setOutputFormat(MongoDBOutputFormat.class);
        MongoDBConfiguration.configureMongoDB(jobConf, database, collection);
        
    }

    public Tuple source(Object key, Object value) {
        return new Tuple();
    }

    // {@inheritDoc}
    public void sink(TupleEntry tupleEntry, OutputCollector outputCollector) throws IOException {

        outputCollector.collect(documentFormat, tupleEntry);

    }

    /**
     * Provides hook for subclasses to escape or modify any tuple value before it's sent upstream.
     * @param result
     * @return
     */
    protected Tuple cleanTuple(Tuple result)
    {
        return result;
    }

    
}
