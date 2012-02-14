begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|IndexWriter
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

begin_comment
comment|/**  * Utility class to safely share {@link IndexSearcher} instances across multiple  * threads, while periodically reopening. This class ensures each searcher is  * closed only once all threads have finished using it.  *   *<p>  * Use {@link #acquire} to obtain the current searcher, and {@link #release} to  * release it, like this:  *   *<pre class="prettyprint">  * IndexSearcher s = manager.acquire();  * try {  *   // Do searching, doc retrieval, etc. with s  * } finally {  *   manager.release(s);  * }  * // Do not use s after this!  * s = null;  *</pre>  *   *<p>  * In addition you should periodically call {@link #maybeRefresh}. While it's  * possible to call this just before running each query, this is discouraged  * since it penalizes the unlucky queries that do the reopen. It's better to use  * a separate background thread, that periodically calls maybeReopen. Finally,  * be sure to call {@link #close} once you are done.  *   * @see SearcherFactory  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SearcherManager
specifier|public
specifier|final
class|class
name|SearcherManager
extends|extends
name|ReferenceManager
argument_list|<
name|IndexSearcher
argument_list|>
block|{
DECL|field|searcherFactory
specifier|private
specifier|final
name|SearcherFactory
name|searcherFactory
decl_stmt|;
comment|/**    * Creates and returns a new SearcherManager from the given    * {@link IndexWriter}.    *     * @param writer    *          the IndexWriter to open the IndexReader from.    * @param applyAllDeletes    *          If<code>true</code>, all buffered deletes will be applied (made    *          visible) in the {@link IndexSearcher} / {@link DirectoryReader}.    *          If<code>false</code>, the deletes may or may not be applied, but    *          remain buffered (in IndexWriter) so that they will be applied in    *          the future. Applying deletes can be costly, so if your app can    *          tolerate deleted documents being returned you might gain some    *          performance by passing<code>false</code>. See    *          {@link DirectoryReader#openIfChanged(DirectoryReader, IndexWriter, boolean)}.    * @param searcherFactory    *          An optional {@link SearcherFactory}. Pass<code>null</code> if you    *          don't require the searcher to be warmed before going live or other    *          custom behavior.    *     * @throws IOException    */
DECL|method|SearcherManager
specifier|public
name|SearcherManager
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|,
name|SearcherFactory
name|searcherFactory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|searcherFactory
operator|==
literal|null
condition|)
block|{
name|searcherFactory
operator|=
operator|new
name|SearcherFactory
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|searcherFactory
operator|=
name|searcherFactory
expr_stmt|;
name|current
operator|=
name|searcherFactory
operator|.
name|newSearcher
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
name|applyAllDeletes
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decRef
specifier|protected
name|void
name|decRef
parameter_list|(
name|IndexSearcher
name|reference
parameter_list|)
throws|throws
name|IOException
block|{
name|reference
operator|.
name|getIndexReader
argument_list|()
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|refreshIfNeeded
specifier|protected
name|IndexSearcher
name|refreshIfNeeded
parameter_list|(
name|IndexSearcher
name|referenceToRefresh
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexReader
name|r
init|=
name|referenceToRefresh
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
specifier|final
name|IndexReader
name|newReader
init|=
operator|(
name|r
operator|instanceof
name|DirectoryReader
operator|)
condition|?
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
operator|(
name|DirectoryReader
operator|)
name|r
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|newReader
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|searcherFactory
operator|.
name|newSearcher
argument_list|(
name|newReader
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|tryIncRef
specifier|protected
name|boolean
name|tryIncRef
parameter_list|(
name|IndexSearcher
name|reference
parameter_list|)
block|{
return|return
name|reference
operator|.
name|getIndexReader
argument_list|()
operator|.
name|tryIncRef
argument_list|()
return|;
block|}
comment|/**    * Creates and returns a new SearcherManager from the given {@link Directory}.     * @param dir the directory to open the DirectoryReader on.    * @param searcherFactory An optional {@link SearcherFactory}. Pass    *<code>null</code> if you don't require the searcher to be warmed    *        before going live or other custom behavior.    *            * @throws IOException    */
DECL|method|SearcherManager
specifier|public
name|SearcherManager
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SearcherFactory
name|searcherFactory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|searcherFactory
operator|==
literal|null
condition|)
block|{
name|searcherFactory
operator|=
operator|new
name|SearcherFactory
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|searcherFactory
operator|=
name|searcherFactory
expr_stmt|;
name|current
operator|=
name|searcherFactory
operator|.
name|newSearcher
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns<code>true</code> if no changes have occured since this searcher    * ie. reader was opened, otherwise<code>false</code>.    * @see DirectoryReader#isCurrent()     */
DECL|method|isSearcherCurrent
specifier|public
name|boolean
name|isSearcherCurrent
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|IndexSearcher
name|searcher
init|=
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|IndexReader
name|r
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
return|return
name|r
operator|instanceof
name|DirectoryReader
condition|?
operator|(
operator|(
name|DirectoryReader
operator|)
name|r
operator|)
operator|.
name|isCurrent
argument_list|()
else|:
literal|true
return|;
block|}
finally|finally
block|{
name|release
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

