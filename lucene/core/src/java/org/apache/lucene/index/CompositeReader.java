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
name|util
operator|.
name|List
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
name|search
operator|.
name|SearcherManager
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|*
import|;
end_import

begin_comment
comment|/** Instances of this reader type can only   be used to get stored fields from the underlying AtomicReaders,   but it is not possible to directly retrieve postings. To do that, get   the sub-readers via {@link #getSequentialSubReaders}.   Alternatively, you can mimic an {@link AtomicReader} (with a serious slowdown),   by wrapping composite readers with {@link SlowCompositeReaderWrapper}.<p>IndexReader instances for indexes on disk are usually constructed  with a call to one of the static<code>DirectoryReader.open()</code> methods,  e.g. {@link DirectoryReader#open(Directory)}. {@link DirectoryReader} implements  the {@code CompositeReader} interface, it is not possible to directly get postings.<p> Concrete subclasses of IndexReader are usually constructed with a call to  one of the static<code>open()</code> methods, e.g. {@link  DirectoryReader#open(Directory)}.<p> For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique  document in the index.  These document numbers are ephemeral -- they may change  as documents are added to and deleted from an index.  Clients should thus not  rely on a given document having the same number between sessions.<p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  IndexReader} instances are completely thread  safe, meaning multiple threads can call any of its methods,  concurrently.  If your application requires external  synchronization, you should<b>not</b> synchronize on the<code>IndexReader</code> instance; use your own  (non-Lucene) objects instead. */
end_comment

begin_class
DECL|class|CompositeReader
specifier|public
specifier|abstract
class|class
name|CompositeReader
extends|extends
name|IndexReader
block|{
DECL|field|readerContext
specifier|private
specifier|volatile
name|CompositeReaderContext
name|readerContext
init|=
literal|null
decl_stmt|;
comment|// lazy init
DECL|method|CompositeReader
specifier|protected
name|CompositeReader
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|IndexReader
argument_list|>
name|subReaders
init|=
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
assert|assert
name|subReaders
operator|!=
literal|null
assert|;
if|if
condition|(
operator|!
name|subReaders
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|subReaders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|,
name|c
init|=
name|subReaders
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|c
condition|;
operator|++
name|i
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|subReaders
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Expert: returns the sequential sub readers that this    *  reader is logically composed of. It contrast to previous    *  Lucene versions may not return null.    *  If this method returns an empty array, that means this    *  reader is a null reader (for example a MultiReader    *  that has no sub readers).    */
DECL|method|getSequentialSubReaders
specifier|public
specifier|abstract
name|List
argument_list|<
name|?
extends|extends
name|IndexReader
argument_list|>
name|getSequentialSubReaders
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getTopReaderContext
specifier|public
specifier|final
name|CompositeReaderContext
name|getTopReaderContext
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// lazy init without thread safety for perf reasons: Building the readerContext twice does not hurt!
if|if
condition|(
name|readerContext
operator|==
literal|null
condition|)
block|{
assert|assert
name|getSequentialSubReaders
argument_list|()
operator|!=
literal|null
assert|;
name|readerContext
operator|=
name|CompositeReaderContext
operator|.
name|create
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|readerContext
return|;
block|}
block|}
end_class

end_unit

