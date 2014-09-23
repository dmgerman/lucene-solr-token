begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|AlreadyClosedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TestIndexReaderClose
specifier|public
class|class
name|TestIndexReaderClose
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCloseUnderException
specifier|public
name|void
name|testCloseUnderException
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
literal|1000
operator|+
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|iters
condition|;
name|j
operator|++
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|open
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|throwOnClose
init|=
operator|!
name|rarely
argument_list|()
decl_stmt|;
name|LeafReader
name|wrap
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|open
argument_list|)
decl_stmt|;
name|FilterLeafReader
name|reader
init|=
operator|new
name|FilterLeafReader
argument_list|(
name|wrap
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|doClose
argument_list|()
expr_stmt|;
if|if
condition|(
name|throwOnClose
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"BOOM!"
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|int
name|listenerCount
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|boolean
name|faultySet
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|listenerCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|faultySet
operator|=
literal|true
expr_stmt|;
name|reader
operator|.
name|addReaderClosedListener
argument_list|(
operator|new
name|FaultyListener
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|count
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|reader
operator|.
name|addReaderClosedListener
argument_list|(
operator|new
name|CountListener
argument_list|(
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|faultySet
operator|&&
operator|!
name|throwOnClose
condition|)
block|{
name|reader
operator|.
name|addReaderClosedListener
argument_list|(
operator|new
name|FaultyListener
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected Exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|throwOnClose
condition|)
block|{
name|assertEquals
argument_list|(
literal|"BOOM!"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"GRRRRRRRRRRRR!"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|reader
operator|.
name|fields
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"we are closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ex
parameter_list|)
block|{       }
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// call it again
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|count
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|wrap
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testCoreListenerOnWrapper
specifier|public
name|void
name|testCoreListenerOnWrapper
parameter_list|()
throws|throws
name|IOException
block|{
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|newDirectory
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
operator|.
name|w
operator|.
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|LeafReader
name|leafReader
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numListeners
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|LeafReader
operator|.
name|CoreClosedListener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
name|numListeners
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numListeners
condition|;
operator|++
name|i
control|)
block|{
name|CountCoreListener
name|listener
init|=
operator|new
name|CountCoreListener
argument_list|(
name|counter
argument_list|)
decl_stmt|;
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|leafReader
operator|.
name|addCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|leafReader
operator|.
name|addCoreClosedListener
argument_list|(
name|listeners
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|listeners
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|removed
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numListeners
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|listeners
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|removed
condition|;
operator|++
name|i
control|)
block|{
name|leafReader
operator|.
name|removeCoreClosedListener
argument_list|(
name|listeners
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|numListeners
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure listeners are registered on the wrapped reader and that closing any of them has the same effect
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|leafReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|removed
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|w
operator|.
name|getDirectory
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|CountCoreListener
specifier|private
specifier|static
specifier|final
class|class
name|CountCoreListener
implements|implements
name|LeafReader
operator|.
name|CoreClosedListener
block|{
DECL|field|count
specifier|private
specifier|final
name|AtomicInteger
name|count
decl_stmt|;
DECL|method|CountCoreListener
specifier|public
name|CountCoreListener
parameter_list|(
name|AtomicInteger
name|count
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|Object
name|coreCacheKey
parameter_list|)
block|{
name|count
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|CountListener
specifier|private
specifier|static
specifier|final
class|class
name|CountListener
implements|implements
name|IndexReader
operator|.
name|ReaderClosedListener
block|{
DECL|field|count
specifier|private
specifier|final
name|AtomicInteger
name|count
decl_stmt|;
DECL|method|CountListener
specifier|public
name|CountListener
parameter_list|(
name|AtomicInteger
name|count
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|count
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|FaultyListener
specifier|private
specifier|static
specifier|final
class|class
name|FaultyListener
implements|implements
name|IndexReader
operator|.
name|ReaderClosedListener
block|{
annotation|@
name|Override
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"GRRRRRRRRRRRR!"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

