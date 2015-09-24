package simpledb;

import java.io.*;
import java.nio.Buffer;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    File file;
    TupleDesc tupleDesc;
    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        file = f;
        tupleDesc = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        return file.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        return tupleDesc;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        try {
            RandomAccessFile read = new RandomAccessFile(file,"r");
            int offset = BufferPool.PAGE_SIZE * pid.pageNumber();
            byte[] content = new byte[BufferPool.PAGE_SIZE];
            read.readFully(content, offset, BufferPool.PAGE_SIZE);
            read.close();
            return new HeapPage((HeapPageId)pid,content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        return (int) Math.ceil(this.file.length()/ BufferPool.PAGE_SIZE);
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        return null;
    }

    class HeapFileIterator implements DbFileIterator{

        private int curPageNo;
        private PageId pID;
        private Page page;
        private Iterator<Tuple> tupleIt;
        private TransactionId tID;
        private HeapFile heapFile;

        private void setTid(TransactionId tid){
            tID = tid;
        }

        @Override
        public void open() throws DbException, TransactionAbortedException {
            curPageNo = 0;
            pID = new HeapPageId(getId(),curPageNo);
            page = Database.getBufferPool().getPage(tID,pID,null);
            HeapPage hPage = (HeapPage) page;
            tupleIt = hPage.iterator();
        }

        @Override
        public boolean hasNext() throws DbException, TransactionAbortedException {
            if(tupleIt != null){
                if(tupleIt.hasNext()){
                    return true;
                }else{
                    curPageNo++;
                    pID = new HeapPageId(getId(),curPageNo);
                    page = Database.getBufferPool().getPage(tID,pID,null);
                    HeapPage hPage = (HeapPage) page;
                    tupleIt = hPage.iterator();
                    return hasNext();
                }
            }

        }

        @Override
        public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
            if (this.tupleIt != null){
                return tupleIt.next();
            }
            else{
                throw new NoSuchElementException();
            }
        }

        @Override
        public void rewind() throws DbException, TransactionAbortedException {

        }

        @Override
        public void close() {

        }
    }
}

