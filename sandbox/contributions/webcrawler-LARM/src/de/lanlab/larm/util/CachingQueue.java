begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_package
DECL|package|de.lanlab.larm.util
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
DECL|class|StoreException
class|class
name|StoreException
extends|extends
name|RuntimeException
block|{
DECL|field|origException
name|Exception
name|origException
decl_stmt|;
comment|/**      * Constructor for the StoreException object      *      * @param e  Description of the Parameter      */
DECL|method|StoreException
specifier|public
name|StoreException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|origException
operator|=
name|e
expr_stmt|;
block|}
comment|/**      * Gets the message attribute of the StoreException object      *      * @return   The message value      */
DECL|method|getMessage
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|origException
operator|.
name|getMessage
argument_list|()
return|;
block|}
comment|/**      * Description of the Method      */
DECL|method|printStackTrace
specifier|public
name|void
name|printStackTrace
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"StoreException occured with reason: "
operator|+
name|origException
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|origException
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/**  * internal class that represents one block within a queue  *  * @author    Clemens Marschner  * @created   3. Januar 2002  */
end_comment

begin_class
DECL|class|QueueBlock
class|class
name|QueueBlock
block|{
comment|/**      * the elements section will be set to null if it is on disk Vector elements      * must be Serializable      */
DECL|field|elements
name|LinkedList
name|elements
decl_stmt|;
comment|/**      * Anzahl Elemente im Block. Kopie von elements.size()      */
DECL|field|size
name|int
name|size
decl_stmt|;
comment|/**      * maximale Blockgröße      */
DECL|field|maxSize
name|int
name|maxSize
decl_stmt|;
comment|/**      * if set, elements is null and block was written to file      */
DECL|field|onDisk
name|boolean
name|onDisk
decl_stmt|;
comment|/**      * Blockname      */
DECL|field|name
name|String
name|name
decl_stmt|;
comment|/**      * initialisiert den Block      *      * @param name     Der Blockname (muss eindeutig sein, sonst Kollision auf      *      Dateiebene)      * @param maxSize  maximale Blockgröße. Über- und Unterläufe werden durch      *      Exceptions behandelt      */
DECL|method|QueueBlock
specifier|public
name|QueueBlock
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|maxSize
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|onDisk
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|elements
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
comment|/**      * serialisiert und speichert den Block auf Platte      *      * @exception StoreException  Description of the Exception      */
DECL|method|store
specifier|public
name|void
name|store
parameter_list|()
throws|throws
name|StoreException
block|{
try|try
block|{
name|ObjectOutputStream
name|o
init|=
operator|new
name|ObjectOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|getFileName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|o
operator|.
name|writeObject
argument_list|(
name|elements
argument_list|)
expr_stmt|;
name|elements
operator|=
literal|null
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|onDisk
operator|=
literal|true
expr_stmt|;
comment|//System.out.println("CachingQueue.store: Block stored");
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"CachingQueue.store: IOException"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return   the filename of the block      */
DECL|method|getFileName
name|String
name|getFileName
parameter_list|()
block|{
comment|// package protected!
return|return
literal|"cachingqueue/"
operator|+
name|name
operator|+
literal|".cqb"
return|;
block|}
comment|/**      * load the block from disk      *      * @exception StoreException  Description of the Exception      */
DECL|method|load
specifier|public
name|void
name|load
parameter_list|()
throws|throws
name|StoreException
block|{
try|try
block|{
name|ObjectInputStream
name|i
init|=
operator|new
name|ObjectInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|getFileName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|elements
operator|=
operator|(
name|LinkedList
operator|)
name|i
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|i
operator|.
name|close
argument_list|()
expr_stmt|;
name|onDisk
operator|=
literal|false
expr_stmt|;
name|size
operator|=
name|elements
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|getFileName
argument_list|()
argument_list|)
operator|.
name|delete
argument_list|()
operator|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"CachingQueue.load: file could not be deleted"
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("CachingQueue.load: Block loaded");
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"CachingQueue.load: Exception "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" occured"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * inserts an object at the start of the queue must be synchronized by      * calling class to be thread safe      *      * @param o                   Description of the Parameter      * @exception StoreException  Description of the Exception      */
DECL|method|insert
specifier|public
name|void
name|insert
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|StoreException
block|{
if|if
condition|(
name|onDisk
condition|)
block|{
name|load
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|>=
name|maxSize
condition|)
block|{
throw|throw
operator|new
name|OverflowException
argument_list|()
throw|;
block|}
name|elements
operator|.
name|addFirst
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
comment|/**      * gibt das letzte Element aus der Queue zurück und löscht dieses must be      * made synchronized by calling class to be thread safe      *      * @return                        Description of the Return Value      * @exception UnderflowException  Description of the Exception      * @exception StoreException      Description of the Exception      */
DECL|method|remove
specifier|public
name|Object
name|remove
parameter_list|()
throws|throws
name|UnderflowException
throws|,
name|StoreException
block|{
if|if
condition|(
name|onDisk
condition|)
block|{
name|load
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|UnderflowException
argument_list|()
throw|;
block|}
name|size
operator|--
expr_stmt|;
return|return
name|elements
operator|.
name|removeLast
argument_list|()
return|;
block|}
comment|/**      * @return   the number of elements in the block      */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * destructor. Assures that all files are deleted, even if the queue was not      * empty at the time when the program ended      */
DECL|method|finalize
specifier|public
name|void
name|finalize
parameter_list|()
block|{
comment|// System.err.println("finalize von " + name + " called");
if|if
condition|(
name|onDisk
condition|)
block|{
comment|// temp-Datei löschen. Passiert, wenn z.B. eine Exception aufgetreten ist
comment|// System.err.println("CachingQueue.finalize von Block " + name + ": lösche Datei");
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|getFileName
argument_list|()
argument_list|)
operator|.
name|delete
argument_list|()
operator|)
condition|)
block|{
comment|// Dateifehler möglich durch Exception: ignorieren
comment|// System.err.println("CachingQueue.finalize: file could not be deleted although onDisk was true");
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|/**  * this class holds a queue whose data is kept on disk whenever possible.  * It's a single ended queue, meaning data can only be added at the front and  * taken from the back. the queue itself is divided into blocks. Only the first  * and last blocks are kept in main memory, the rest is stored on disk. Only a  * LinkedList entry is kept in memory then.  * Blocks are swapped if an overflow (in case of insertions) or underflow (in case  * of removals) occur.<br>  *  *<pre>  *         +---+---+---+---+-+  *  put -> | M | S | S | S |M| -> remove  *         +---+---+---+---+-+  *</pre>  * the maximum number of entries can be specified with the blockSize parameter. Thus,  * the queue actually holds a maximum number of 2 x blockSize objects in main memory,  * plus a few bytes for each block.<br>  * The objects contained in the blocks are stored with the standard Java  * serialization mechanism  * The files are named "cachingqueue\\Queuename_BlockNumber.cqb"  * note that the class is not synchronized  * @author    Clemens Marschner  * @created   3. Januar 2002  */
end_comment

begin_class
DECL|class|CachingQueue
specifier|public
class|class
name|CachingQueue
implements|implements
name|Queue
block|{
comment|/**      * the Blocks      */
DECL|field|queueBlocks
name|LinkedList
name|queueBlocks
decl_stmt|;
comment|/**      * fast access to the first block      */
DECL|field|first
name|QueueBlock
name|first
init|=
literal|null
decl_stmt|;
comment|/**      * fast access to the last block      */
DECL|field|last
name|QueueBlock
name|last
init|=
literal|null
decl_stmt|;
comment|/**      * maximum block size      */
DECL|field|blockSize
name|int
name|blockSize
decl_stmt|;
comment|/**      * "primary key" identity count for each block      */
DECL|field|blockCount
name|int
name|blockCount
init|=
literal|0
decl_stmt|;
comment|/**      * active blocks      */
DECL|field|numBlocks
name|int
name|numBlocks
init|=
literal|0
decl_stmt|;
comment|/**      * queue name      */
DECL|field|name
name|String
name|name
decl_stmt|;
comment|/**      * total number of objects      */
DECL|field|size
name|int
name|size
decl_stmt|;
comment|/**      * init      *      * @param name the name of the queue, used in files names      * @param blockSize maximum number of objects stored in one block      */
DECL|method|CachingQueue
specifier|public
name|CachingQueue
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|blockSize
parameter_list|)
block|{
name|queueBlocks
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
comment|// FIXME: the name of the caching queue directory needs to be in properties
name|File
name|cq
init|=
operator|new
name|File
argument_list|(
literal|"cachingqueue"
argument_list|)
decl_stmt|;
name|cq
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
comment|/**      * inserts an object to the front of the queue      *      * @param o                   the object to be inserted. must implement Serializable      * @exception StoreException  encapsulates Exceptions that occur when writing to hard disk      */
DECL|method|insert
specifier|public
specifier|synchronized
name|void
name|insert
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|StoreException
block|{
if|if
condition|(
name|last
operator|==
literal|null
operator|&&
name|first
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|last
operator|=
name|newBlock
argument_list|()
expr_stmt|;
name|queueBlocks
operator|.
name|addFirst
argument_list|(
name|first
argument_list|)
expr_stmt|;
name|numBlocks
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|last
operator|==
literal|null
operator|&&
name|first
operator|!=
literal|null
condition|)
block|{
comment|// affirm((last==null&& first==null) || (last!= null&& first!=null));
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error in CachingQueue: last!=first==null"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|first
operator|.
name|size
argument_list|()
operator|>=
name|blockSize
condition|)
block|{
comment|// save block and create a new one
name|QueueBlock
name|newBlock
init|=
name|newBlock
argument_list|()
decl_stmt|;
name|numBlocks
operator|++
expr_stmt|;
if|if
condition|(
name|last
operator|!=
name|first
condition|)
block|{
name|first
operator|.
name|store
argument_list|()
expr_stmt|;
block|}
name|queueBlocks
operator|.
name|addFirst
argument_list|(
name|newBlock
argument_list|)
expr_stmt|;
name|first
operator|=
name|newBlock
expr_stmt|;
block|}
name|first
operator|.
name|insert
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
comment|/**      * returns the last object from the queue      *      * @return                     the object returned      *      * @exception StoreException   Description of the Exception      * @exception UnderflowException if the queue was empty      */
DECL|method|remove
specifier|public
specifier|synchronized
name|Object
name|remove
parameter_list|()
throws|throws
name|StoreException
throws|,
name|UnderflowException
block|{
if|if
condition|(
name|last
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnderflowException
argument_list|()
throw|;
block|}
if|if
condition|(
name|last
operator|.
name|size
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|queueBlocks
operator|.
name|removeLast
argument_list|()
expr_stmt|;
name|numBlocks
operator|--
expr_stmt|;
if|if
condition|(
name|numBlocks
operator|==
literal|1
condition|)
block|{
name|last
operator|=
name|first
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numBlocks
operator|==
literal|0
condition|)
block|{
name|first
operator|=
name|last
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|UnderflowException
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|numBlocks
operator|<
literal|0
condition|)
block|{
comment|// affirm(numBlocks>= 0)
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"CachingQueue.remove: numBlocks<0!"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|UnderflowException
argument_list|()
throw|;
block|}
else|else
block|{
name|last
operator|=
operator|(
name|QueueBlock
operator|)
name|queueBlocks
operator|.
name|getLast
argument_list|()
expr_stmt|;
block|}
block|}
operator|--
name|size
expr_stmt|;
return|return
name|last
operator|.
name|remove
argument_list|()
return|;
block|}
comment|/**      * not supported      *      * @param c  Description of the Parameter      */
DECL|method|insertMultiple
specifier|public
name|void
name|insertMultiple
parameter_list|(
name|java
operator|.
name|util
operator|.
name|Collection
name|c
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * creates a new block      *      * @return   Description of the Return Value      */
DECL|method|newBlock
specifier|private
name|QueueBlock
name|newBlock
parameter_list|()
block|{
return|return
operator|new
name|QueueBlock
argument_list|(
name|name
operator|+
literal|"_"
operator|+
name|blockCount
operator|++
argument_list|,
name|blockSize
argument_list|)
return|;
block|}
comment|/**      * total number of objects contained in the queue      *      * @return   Description of the Return Value      */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * testing      *      * @param args  The command line arguments      */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test1: "
operator|+
name|CachingQueueTester
operator|.
name|testUnderflow
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test2: "
operator|+
name|CachingQueueTester
operator|.
name|testInsert
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test3: "
operator|+
name|CachingQueueTester
operator|.
name|testBufReadWrite
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test4: "
operator|+
name|CachingQueueTester
operator|.
name|testBufReadWrite2
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test5: "
operator|+
name|CachingQueueTester
operator|.
name|testUnderflow2
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test6: "
operator|+
name|CachingQueueTester
operator|.
name|testBufReadWrite3
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test7: "
operator|+
name|CachingQueueTester
operator|.
name|testExceptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/**  * Testklasse TODO: auslagern und per JUnit handhaben  *  * @author    Administrator  * @created   3. Januar 2002  */
end_comment

begin_class
DECL|class|AssertionFailedException
class|class
name|AssertionFailedException
extends|extends
name|RuntimeException
block|{ }
end_class

begin_comment
comment|/**  * Testklasse. Enthält einige Tests für die Funktionalität der CachingQueue  *  * @author    Administrator  * @created   3. Januar 2002  */
end_comment

begin_class
DECL|class|CachingQueueTester
class|class
name|CachingQueueTester
block|{
comment|/**      * A unit test for JUnit      *      * @return   Description of the Return Value      */
DECL|method|testUnderflow
specifier|public
specifier|static
name|boolean
name|testUnderflow
parameter_list|()
block|{
name|CachingQueue
name|cq
init|=
operator|new
name|CachingQueue
argument_list|(
literal|"testQueue1"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
try|try
block|{
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnderflowException
name|e
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * A unit test for JUnit      *      * @return   Description of the Return Value      */
DECL|method|testInsert
specifier|public
specifier|static
name|boolean
name|testInsert
parameter_list|()
block|{
name|CachingQueue
name|cq
init|=
operator|new
name|CachingQueue
argument_list|(
literal|"testQueue2"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|test
init|=
literal|"Test1"
decl_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
return|return
operator|(
name|cq
operator|.
name|remove
argument_list|()
operator|==
name|test
operator|)
return|;
block|}
comment|/**      * A unit test for JUnit      *      * @return   Description of the Return Value      */
DECL|method|testBufReadWrite
specifier|public
specifier|static
name|boolean
name|testBufReadWrite
parameter_list|()
block|{
name|CachingQueue
name|cq
init|=
operator|new
name|CachingQueue
argument_list|(
literal|"testQueue3"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|String
name|test1
init|=
literal|"Test1"
decl_stmt|;
name|String
name|test2
init|=
literal|"Test2"
decl_stmt|;
name|String
name|test3
init|=
literal|"Test3"
decl_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test1
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test3
argument_list|)
expr_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
return|return
operator|(
name|cq
operator|.
name|remove
argument_list|()
operator|==
name|test3
operator|)
return|;
block|}
comment|/**      * A unit test for JUnit      *      * @return   Description of the Return Value      */
DECL|method|testBufReadWrite2
specifier|public
specifier|static
name|boolean
name|testBufReadWrite2
parameter_list|()
block|{
name|CachingQueue
name|cq
init|=
operator|new
name|CachingQueue
argument_list|(
literal|"testQueue4"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|String
name|test1
init|=
literal|"Test1"
decl_stmt|;
name|String
name|test2
init|=
literal|"Test2"
decl_stmt|;
name|String
name|test3
init|=
literal|"Test3"
decl_stmt|;
name|String
name|test4
init|=
literal|"Test4"
decl_stmt|;
name|String
name|test5
init|=
literal|"Test5"
decl_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test1
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test3
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test4
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test5
argument_list|)
expr_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|String
name|t
init|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
decl_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test1
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test2
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test3
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test4
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
return|return
operator|(
name|t
operator|.
name|equals
argument_list|(
name|test5
argument_list|)
operator|)
return|;
block|}
comment|/**      * Description of the Method      *      * @param expr  Description of the Parameter      */
DECL|method|affirm
specifier|public
specifier|static
name|void
name|affirm
parameter_list|(
name|boolean
name|expr
parameter_list|)
block|{
if|if
condition|(
operator|!
name|expr
condition|)
block|{
throw|throw
operator|new
name|AssertionFailedException
argument_list|()
throw|;
block|}
block|}
comment|/**      * A unit test for JUnit      *      * @return   Description of the Return Value      */
DECL|method|testUnderflow2
specifier|public
specifier|static
name|boolean
name|testUnderflow2
parameter_list|()
block|{
name|CachingQueue
name|cq
init|=
operator|new
name|CachingQueue
argument_list|(
literal|"testQueue5"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|String
name|test1
init|=
literal|"Test1"
decl_stmt|;
name|String
name|test2
init|=
literal|"Test2"
decl_stmt|;
name|String
name|test3
init|=
literal|"Test3"
decl_stmt|;
name|String
name|test4
init|=
literal|"Test4"
decl_stmt|;
name|String
name|test5
init|=
literal|"Test5"
decl_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test1
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test3
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test4
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test5
argument_list|)
expr_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|remove
argument_list|()
operator|.
name|equals
argument_list|(
name|test1
argument_list|)
argument_list|)
expr_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|remove
argument_list|()
operator|.
name|equals
argument_list|(
name|test2
argument_list|)
argument_list|)
expr_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|remove
argument_list|()
operator|.
name|equals
argument_list|(
name|test3
argument_list|)
argument_list|)
expr_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|remove
argument_list|()
operator|.
name|equals
argument_list|(
name|test4
argument_list|)
argument_list|)
expr_stmt|;
name|affirm
argument_list|(
name|cq
operator|.
name|remove
argument_list|()
operator|.
name|equals
argument_list|(
name|test5
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnderflowException
name|e
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * A unit test for JUnit      *      * @return   Description of the Return Value      */
DECL|method|testBufReadWrite3
specifier|public
specifier|static
name|boolean
name|testBufReadWrite3
parameter_list|()
block|{
name|CachingQueue
name|cq
init|=
operator|new
name|CachingQueue
argument_list|(
literal|"testQueue4"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|test1
init|=
literal|"Test1"
decl_stmt|;
name|String
name|test2
init|=
literal|"Test2"
decl_stmt|;
name|String
name|test3
init|=
literal|"Test3"
decl_stmt|;
name|String
name|test4
init|=
literal|"Test4"
decl_stmt|;
name|String
name|test5
init|=
literal|"Test5"
decl_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test1
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test3
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test4
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test5
argument_list|)
expr_stmt|;
name|String
name|t
init|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
decl_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test1
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test2
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test3
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test4
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
operator|(
name|t
operator|.
name|equals
argument_list|(
name|test5
argument_list|)
operator|)
return|;
block|}
comment|/**      * A unit test for JUnit      *      * @return   Description of the Return Value      */
DECL|method|testExceptions
specifier|public
specifier|static
name|boolean
name|testExceptions
parameter_list|()
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|CachingQueue
name|cq
init|=
operator|new
name|CachingQueue
argument_list|(
literal|"testQueue5"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|test1
init|=
literal|"Test1"
decl_stmt|;
name|String
name|test2
init|=
literal|"Test2"
decl_stmt|;
name|String
name|test3
init|=
literal|"Test3"
decl_stmt|;
name|String
name|test4
init|=
literal|"Test4"
decl_stmt|;
name|String
name|test5
init|=
literal|"Test5"
decl_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test1
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test3
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test4
argument_list|)
expr_stmt|;
name|cq
operator|.
name|insert
argument_list|(
name|test5
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
literal|"testQueue5_1.cqb"
argument_list|)
operator|.
name|delete
argument_list|()
operator|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"CachingQueueTester.textExceptions: Store 1 nicht vorhanden. Filename geändert?"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
literal|"testQueue5_2.cqb"
argument_list|)
operator|.
name|delete
argument_list|()
operator|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"CachingQueueTester.textExceptions: Store 2 nicht vorhanden. Filename geändert?"
argument_list|)
expr_stmt|;
block|}
name|String
name|t
init|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
decl_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test1
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test2
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test3
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test4
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|(
name|String
operator|)
name|cq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|affirm
argument_list|(
name|t
operator|.
name|equals
argument_list|(
name|test5
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StoreException
name|e
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|cq
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
comment|// finalizer müssten aufgerufen werden
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

