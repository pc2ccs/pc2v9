package edu.csus.ecs.pc2.core.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * A collection of test case file name sets (pairs) for a Problem.
 * Note that per the Collection/Iterator Design Pattern, the fact that the collection is stored in a
 * particular data structure is hidden from the outside world; the
 * only things the outside world can do are defined in the public methods of this class, and
 * those things do NOT include accessing any information about the internal storage data type.
 * In particular, external clients cannot rely on using any sort of "index" related to the position
 * of a particular test case in the collection, because a "collection" is by definition not
 * a linear data structure.
 */
public class TestCaseFileNameCollection implements Collection<TestCaseFileNameSet> {
    
    
    //the internal storage for the test sets in the collection (see the class header comments...)
    private Vector<TestCaseFileNameSet> testCaseFileNameSets = new Vector<TestCaseFileNameSet>();


    @Override
    /**
     * Returns the number of Test Cases (TestCaseFileNameSets) in the collection.
     */
    public int size() {
        return testCaseFileNameSets.size();
    }

    @Override
    /**
     * Returns true if the size of the Collection (that is, the number of TestCaseFileNameSets)
     * is less than or equal to zero; false if it is greater than zero.
     */
    public boolean isEmpty() {
        return testCaseFileNameSets.size() <= 0;
    }

    /**
     * Returns true if the specified Object is a TestCaseFileNameSet and that set is currently
     * in this Collection; false otherwise.
     */
    @Override
    public boolean contains(Object obj) {
        if (obj instanceof TestCaseFileNameSet) {
            return (testCaseFileNameSets.contains((TestCaseFileNameSet) obj));
        } else {
            return false ;
        }
    }

    /**
     * Returns an iterator for this Collection.
     */
    @Override
    public Iterator<TestCaseFileNameSet> iterator() {
        return new TestCaseFileNameVectorIterator();
    }

    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * If the specified Object is a TestCaseFileNameSet, adds it to the collection of Test Case File Name Sets;
     * otherwise, does nothing.
     * @return true if the specified Object is a TestCaseFileNameSet and adding the test case to the Collection was successful; false otherwise.
     */
    @Override
    public boolean add(TestCaseFileNameSet tc) {
        if (tc instanceof TestCaseFileNameSet) {
            return testCaseFileNameSets.add(tc);            
        } else {
            return false ;
        }

    }

    /**
     * Removes the specified test case from the collection.
     * @return true if the specified Object is a TestCaseFileNameSet, was present in the Collection, and is now removed;
     * false otherwise.
     */
    @Override
    public boolean remove(Object obj) {
        if (obj instanceof TestCaseFileNameSet) {
            return testCaseFileNameSets.remove((TestCaseFileNameSet) obj);            
        } else {
            return false;
        }

    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends TestCaseFileNameSet> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Returns a deep clone of this Collection.
     */
    @Override
    public TestCaseFileNameCollection clone() {
        TestCaseFileNameCollection copy = new TestCaseFileNameCollection();
        for (TestCaseFileNameSet tc : this) {
            copy.add((TestCaseFileNameSet) tc.clone());
        }
        return null;
    }
    
    /**
     * A class which provides iteration over a Vector of TestCaseFileNameSets.
     * Note that the fact that the iterator is over a VECTOR is invisible to clients
     * per the Iterator design pattern, clients can only know how to check "hasNext() 
     * and "getNext()" (a.k.a "next()).
     * <p>
     * If for some reason a decision was made to change the internal storage for the 
     * collection to something other than a Vector, all that would be required is to
     * implement a new private iterator class for that new data structure.
     * 
     * @author John
     *
     */
    private class TestCaseFileNameVectorIterator implements Iterator<TestCaseFileNameSet> {

        private int curIndex;
        
        TestCaseFileNameVectorIterator() {
            curIndex = -1;
        }
        
        /**
         * Returns true if there is at least one element in the collection that has not been
         * returned by this iterator via the "next()" method; false otherwise.
         */
        @Override
        public boolean hasNext() {
            if (testCaseFileNameSets.size() <= 0) {
                return false;
            }
            return !(curIndex == (testCaseFileNameSets.size() - 1));
            
        }

        /**
         * Returns the next available TestCaseFileNameSet in this collection.
         * Note that it is the user's responsibility to use "hasNext()" to verify 
         * there is still an available element before calling this method.
         */
        @Override
        public TestCaseFileNameSet next() {
            curIndex ++ ;
            return (testCaseFileNameSets.get(curIndex));
        }

        @Override
        public void remove() {
            // TODO Auto-generated method stub
            // added to eliminate java error
        }
        
    }

}
