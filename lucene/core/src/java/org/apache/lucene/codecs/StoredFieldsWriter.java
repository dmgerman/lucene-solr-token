begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not  * use this file except in compliance with the License. You may obtain a copy of  * the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|FieldInfo
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
name|FieldInfos
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
name|MergeState
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
name|StorableField
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
name|StoredDocument
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
name|Bits
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
name|AtomicReader
import|;
end_import

begin_comment
comment|/**  * Codec API for writing stored fields:  *<p>  *<ol>  *<li>For every document, {@link #startDocument(int)} is called,  *       informing the Codec how many fields will be written.  *<li>{@link #writeField(FieldInfo, StorableField)} is called for   *       each field in the document.  *<li>After all documents have been written, {@link #finish(FieldInfos, int)}   *       is called for verification/sanity-checks.  *<li>Finally the writer is closed ({@link #close()})  *</ol>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|StoredFieldsWriter
specifier|public
specifier|abstract
class|class
name|StoredFieldsWriter
implements|implements
name|Closeable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|StoredFieldsWriter
specifier|protected
name|StoredFieldsWriter
parameter_list|()
block|{   }
comment|/** Called before writing the stored fields of the document.    *  {@link #writeField(FieldInfo, StorableField)} will be called    *<code>numStoredFields</code> times. Note that this is    *  called even if the document has no stored fields, in    *  this case<code>numStoredFields</code> will be zero. */
DECL|method|startDocument
specifier|public
specifier|abstract
name|void
name|startDocument
parameter_list|(
name|int
name|numStoredFields
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Writes a single stored field. */
DECL|method|writeField
specifier|public
specifier|abstract
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|StorableField
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Aborts writing entirely, implementation should remove    *  any partially-written files, etc. */
DECL|method|abort
specifier|public
specifier|abstract
name|void
name|abort
parameter_list|()
function_decl|;
comment|/** Called before {@link #close()}, passing in the number    *  of documents that were written. Note that this is     *  intentionally redundant (equivalent to the number of    *  calls to {@link #startDocument(int)}, but a Codec should    *  check that this is the case to detect the JRE bug described     *  in LUCENE-1282. */
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|(
name|FieldInfos
name|fis
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Merges in the stored fields from the readers in     *<code>mergeState</code>. The default implementation skips    *  over deleted documents, and uses {@link #startDocument(int)},    *  {@link #writeField(FieldInfo, StorableField)}, and {@link #finish(FieldInfos, int)},    *  returning the number of documents that were written.    *  Implementations can override this method for more sophisticated    *  merging (bulk-byte copying, etc). */
DECL|method|merge
specifier|public
name|int
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
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
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
comment|// skip deleted docs
continue|continue;
block|}
comment|// TODO: this could be more efficient using
comment|// FieldVisitor instead of loading/writing entire
comment|// doc; ie we just have to renumber the field number
comment|// on the fly?
comment|// NOTE: it's very important to first assign to doc then pass it to
comment|// fieldsWriter.addDocument; see LUCENE-1282
name|StoredDocument
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|addDocument
argument_list|(
name|doc
argument_list|,
name|mergeState
operator|.
name|fieldInfos
argument_list|)
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
literal|300
argument_list|)
expr_stmt|;
block|}
block|}
name|finish
argument_list|(
name|mergeState
operator|.
name|fieldInfos
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
return|return
name|docCount
return|;
block|}
comment|/** sugar method for startDocument() + writeField() for every stored field in the document */
DECL|method|addDocument
specifier|protected
specifier|final
name|void
name|addDocument
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|StorableField
argument_list|>
name|doc
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|storedCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StorableField
name|field
range|:
name|doc
control|)
block|{
name|storedCount
operator|++
expr_stmt|;
block|}
name|startDocument
argument_list|(
name|storedCount
argument_list|)
expr_stmt|;
for|for
control|(
name|StorableField
name|field
range|:
name|doc
control|)
block|{
name|writeField
argument_list|(
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

