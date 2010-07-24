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
name|io
operator|.
name|PrintStream
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
name|analysis
operator|.
name|Analyzer
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
name|index
operator|.
name|codecs
operator|.
name|Codec
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
name|Similarity
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
name|ArrayUtil
import|;
end_import

begin_class
DECL|class|DocumentsWriterPerThread
specifier|public
class|class
name|DocumentsWriterPerThread
block|{
comment|/**    * The IndexingChain must define the {@link #getChain(DocumentsWriter)} method    * which returns the DocConsumer that the DocumentsWriter calls to process the    * documents.     */
DECL|class|IndexingChain
specifier|abstract
specifier|static
class|class
name|IndexingChain
block|{
DECL|method|getChain
specifier|abstract
name|DocConsumer
name|getChain
parameter_list|(
name|DocumentsWriterPerThread
name|documentsWriterPerThread
parameter_list|)
function_decl|;
block|}
DECL|field|defaultIndexingChain
specifier|static
specifier|final
name|IndexingChain
name|defaultIndexingChain
init|=
operator|new
name|IndexingChain
argument_list|()
block|{
annotation|@
name|Override
name|DocConsumer
name|getChain
parameter_list|(
name|DocumentsWriterPerThread
name|documentsWriterPerThread
parameter_list|)
block|{
comment|/*       This is the current indexing chain:        DocConsumer / DocConsumerPerThread         --> code: DocFieldProcessor / DocFieldProcessorPerThread           --> DocFieldConsumer / DocFieldConsumerPerThread / DocFieldConsumerPerField             --> code: DocFieldConsumers / DocFieldConsumersPerThread / DocFieldConsumersPerField               --> code: DocInverter / DocInverterPerThread / DocInverterPerField                 --> InvertedDocConsumer / InvertedDocConsumerPerThread / InvertedDocConsumerPerField                   --> code: TermsHash / TermsHashPerThread / TermsHashPerField                     --> TermsHashConsumer / TermsHashConsumerPerThread / TermsHashConsumerPerField                       --> code: FreqProxTermsWriter / FreqProxTermsWriterPerThread / FreqProxTermsWriterPerField                       --> code: TermVectorsTermsWriter / TermVectorsTermsWriterPerThread / TermVectorsTermsWriterPerField                 --> InvertedDocEndConsumer / InvertedDocConsumerPerThread / InvertedDocConsumerPerField                   --> code: NormsWriter / NormsWriterPerThread / NormsWriterPerField               --> code: StoredFieldsWriter / StoredFieldsWriterPerThread / StoredFieldsWriterPerField     */
comment|// Build up indexing chain:
specifier|final
name|TermsHashConsumer
name|termVectorsWriter
init|=
operator|new
name|TermVectorsTermsWriter
argument_list|(
name|documentsWriterPerThread
argument_list|)
decl_stmt|;
specifier|final
name|TermsHashConsumer
name|freqProxWriter
init|=
operator|new
name|FreqProxTermsWriter
argument_list|()
decl_stmt|;
specifier|final
name|InvertedDocConsumer
name|termsHash
init|=
operator|new
name|TermsHash
argument_list|(
name|documentsWriterPerThread
argument_list|,
name|freqProxWriter
argument_list|,
operator|new
name|TermsHash
argument_list|(
name|documentsWriterPerThread
argument_list|,
name|termVectorsWriter
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|NormsWriter
name|normsWriter
init|=
operator|new
name|NormsWriter
argument_list|()
decl_stmt|;
specifier|final
name|DocInverter
name|docInverter
init|=
operator|new
name|DocInverter
argument_list|(
name|documentsWriterPerThread
operator|.
name|docState
argument_list|,
name|termsHash
argument_list|,
name|normsWriter
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocFieldProcessor
argument_list|(
name|documentsWriterPerThread
argument_list|,
name|docInverter
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|class|DocState
specifier|static
class|class
name|DocState
block|{
DECL|field|docWriter
specifier|final
name|DocumentsWriterPerThread
name|docWriter
decl_stmt|;
DECL|field|analyzer
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|maxFieldLength
name|int
name|maxFieldLength
decl_stmt|;
DECL|field|infoStream
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|similarity
name|Similarity
name|similarity
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|field|doc
name|Document
name|doc
decl_stmt|;
DECL|field|maxTermPrefix
name|String
name|maxTermPrefix
decl_stmt|;
DECL|method|DocState
name|DocState
parameter_list|(
name|DocumentsWriterPerThread
name|docWriter
parameter_list|)
block|{
name|this
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
block|}
comment|// Only called by asserts
DECL|method|testPoint
specifier|public
name|boolean
name|testPoint
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
comment|/** Called if we hit an exception at a bad time (when    *  updating the index files) and must discard all    *  currently buffered docs.  This resets our state,    *  discarding any docs added since last flush. */
DECL|method|abort
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"docWriter: now abort"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{       }
comment|// Reset all postings data
name|doAfterFlush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|aborting
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"docWriter: done abort"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|ramAllocator
specifier|final
name|DocumentsWriterRAMAllocator
name|ramAllocator
init|=
operator|new
name|DocumentsWriterRAMAllocator
argument_list|()
decl_stmt|;
DECL|field|parent
specifier|final
name|DocumentsWriter
name|parent
decl_stmt|;
DECL|field|writer
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|directory
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|docState
specifier|final
name|DocState
name|docState
decl_stmt|;
DECL|field|consumer
specifier|final
name|DocConsumer
name|consumer
decl_stmt|;
DECL|field|docFieldProcessor
specifier|private
name|DocFieldProcessor
name|docFieldProcessor
decl_stmt|;
DECL|field|segment
name|String
name|segment
decl_stmt|;
comment|// Current segment we are working on
DECL|field|aborting
name|boolean
name|aborting
decl_stmt|;
comment|// True if an abort is pending
DECL|field|infoStream
specifier|private
specifier|final
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|numDocsInRAM
specifier|private
name|int
name|numDocsInRAM
decl_stmt|;
DECL|field|flushedDocCount
specifier|private
name|int
name|flushedDocCount
decl_stmt|;
DECL|field|flushState
name|SegmentWriteState
name|flushState
decl_stmt|;
DECL|field|sequenceIDs
name|long
index|[]
name|sequenceIDs
init|=
operator|new
name|long
index|[
literal|8
index|]
decl_stmt|;
DECL|field|closedFiles
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|closedFiles
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|numBytesUsed
name|long
name|numBytesUsed
decl_stmt|;
DECL|method|DocumentsWriterPerThread
specifier|public
name|DocumentsWriterPerThread
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|DocumentsWriter
name|parent
parameter_list|,
name|IndexingChain
name|indexingChain
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|parent
operator|.
name|indexWriter
expr_stmt|;
name|this
operator|.
name|infoStream
operator|=
name|parent
operator|.
name|indexWriter
operator|.
name|getInfoStream
argument_list|()
expr_stmt|;
name|this
operator|.
name|docState
operator|=
operator|new
name|DocState
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|docState
operator|.
name|similarity
operator|=
name|parent
operator|.
name|config
operator|.
name|getSimilarity
argument_list|()
expr_stmt|;
name|this
operator|.
name|docState
operator|.
name|maxFieldLength
operator|=
name|parent
operator|.
name|config
operator|.
name|getMaxFieldLength
argument_list|()
expr_stmt|;
name|consumer
operator|=
name|indexingChain
operator|.
name|getChain
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|consumer
operator|instanceof
name|DocFieldProcessor
condition|)
block|{
name|docFieldProcessor
operator|=
operator|(
name|DocFieldProcessor
operator|)
name|consumer
expr_stmt|;
block|}
block|}
DECL|method|setAborting
name|void
name|setAborting
parameter_list|()
block|{
name|aborting
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|addDocument
specifier|public
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|docState
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|docState
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|docState
operator|.
name|docID
operator|=
name|numDocsInRAM
expr_stmt|;
if|if
condition|(
name|segment
operator|==
literal|null
condition|)
block|{
comment|// this call is synchronized on IndexWriter.segmentInfos
name|segment
operator|=
name|writer
operator|.
name|newSegmentName
argument_list|()
expr_stmt|;
assert|assert
name|numDocsInRAM
operator|==
literal|0
assert|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|processDocument
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
if|if
condition|(
operator|!
name|aborting
condition|)
block|{
comment|// mark document as deleted
name|commitDocument
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|consumer
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|setAborting
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|commitDocument
specifier|public
name|void
name|commitDocument
parameter_list|(
name|long
name|sequenceID
parameter_list|)
block|{
if|if
condition|(
name|numDocsInRAM
operator|==
name|sequenceIDs
operator|.
name|length
condition|)
block|{
name|sequenceIDs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|sequenceIDs
argument_list|)
expr_stmt|;
block|}
name|sequenceIDs
index|[
name|numDocsInRAM
index|]
operator|=
name|sequenceID
expr_stmt|;
name|numDocsInRAM
operator|++
expr_stmt|;
block|}
DECL|method|getNumDocsInRAM
name|int
name|getNumDocsInRAM
parameter_list|()
block|{
return|return
name|numDocsInRAM
return|;
block|}
DECL|method|getMinSequenceID
name|long
name|getMinSequenceID
parameter_list|()
block|{
if|if
condition|(
name|numDocsInRAM
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|sequenceIDs
index|[
literal|0
index|]
return|;
block|}
comment|/** Returns true if any of the fields in the current   *  buffered docs have omitTermFreqAndPositions==false */
DECL|method|hasProx
name|boolean
name|hasProx
parameter_list|()
block|{
return|return
operator|(
name|docFieldProcessor
operator|!=
literal|null
operator|)
condition|?
name|docFieldProcessor
operator|.
name|fieldInfos
operator|.
name|hasProx
argument_list|()
else|:
literal|true
return|;
block|}
DECL|method|getCodec
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|flushState
operator|.
name|codec
return|;
block|}
comment|/** Reset after a flush */
DECL|method|doAfterFlush
specifier|private
name|void
name|doAfterFlush
parameter_list|()
throws|throws
name|IOException
block|{
name|segment
operator|=
literal|null
expr_stmt|;
name|numDocsInRAM
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Flush all pending docs to a new segment */
DECL|method|flush
name|SegmentInfo
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|numDocsInRAM
operator|>
literal|0
assert|;
name|flushState
operator|=
operator|new
name|SegmentWriteState
argument_list|(
name|infoStream
argument_list|,
name|directory
argument_list|,
name|segment
argument_list|,
name|docFieldProcessor
operator|.
name|fieldInfos
argument_list|,
name|numDocsInRAM
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getTermIndexInterval
argument_list|()
argument_list|,
name|writer
operator|.
name|codecs
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"flush postings as segment "
operator|+
name|flushState
operator|.
name|segmentName
operator|+
literal|" numDocs="
operator|+
name|numDocsInRAM
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|flush
argument_list|(
name|flushState
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|SegmentInfo
name|si
init|=
operator|new
name|SegmentInfo
argument_list|(
name|flushState
operator|.
name|segmentName
argument_list|,
name|flushState
operator|.
name|numDocs
argument_list|,
name|directory
argument_list|,
literal|false
argument_list|,
name|hasProx
argument_list|()
argument_list|,
name|getCodec
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|long
name|newSegmentSize
init|=
name|si
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
name|String
name|message
init|=
literal|"  ramUsed="
operator|+
name|ramAllocator
operator|.
name|nf
operator|.
name|format
argument_list|(
operator|(
operator|(
name|double
operator|)
name|numBytesUsed
operator|)
operator|/
literal|1024.
operator|/
literal|1024.
argument_list|)
operator|+
literal|" MB"
operator|+
literal|" newFlushedSize="
operator|+
name|newSegmentSize
operator|+
literal|" docs/MB="
operator|+
name|ramAllocator
operator|.
name|nf
operator|.
name|format
argument_list|(
name|numDocsInRAM
operator|/
operator|(
name|newSegmentSize
operator|/
literal|1024.
operator|/
literal|1024.
operator|)
argument_list|)
operator|+
literal|" new/old="
operator|+
name|ramAllocator
operator|.
name|nf
operator|.
name|format
argument_list|(
literal|100.0
operator|*
name|newSegmentSize
operator|/
name|numBytesUsed
argument_list|)
operator|+
literal|"%"
decl_stmt|;
name|message
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|flushedDocCount
operator|+=
name|flushState
operator|.
name|numDocs
expr_stmt|;
name|long
name|maxSequenceID
init|=
name|sequenceIDs
index|[
name|numDocsInRAM
operator|-
literal|1
index|]
decl_stmt|;
name|doAfterFlush
argument_list|()
expr_stmt|;
comment|// Create new SegmentInfo, but do not add to our
comment|// segmentInfos until deletes are flushed
comment|// successfully.
name|SegmentInfo
name|newSegment
init|=
operator|new
name|SegmentInfo
argument_list|(
name|flushState
operator|.
name|segmentName
argument_list|,
name|flushState
operator|.
name|numDocs
argument_list|,
name|directory
argument_list|,
literal|false
argument_list|,
name|hasProx
argument_list|()
argument_list|,
name|getCodec
argument_list|()
argument_list|)
decl_stmt|;
name|newSegment
operator|.
name|setMinSequenceID
argument_list|(
name|sequenceIDs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|newSegment
operator|.
name|setMaxSequenceID
argument_list|(
name|maxSequenceID
argument_list|)
expr_stmt|;
name|IndexWriter
operator|.
name|setDiagnostics
argument_list|(
name|newSegment
argument_list|,
literal|"flush"
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|newSegment
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|setAborting
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Get current segment name we are writing. */
DECL|method|getSegment
name|String
name|getSegment
parameter_list|()
block|{
return|return
name|segment
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|closedFiles
name|List
argument_list|<
name|String
argument_list|>
name|closedFiles
parameter_list|()
block|{
return|return
call|(
name|List
argument_list|<
name|String
argument_list|>
call|)
argument_list|(
operator|(
name|ArrayList
argument_list|<
name|String
argument_list|>
operator|)
name|closedFiles
argument_list|)
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|addOpenFile
name|void
name|addOpenFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|parent
operator|.
name|openFiles
init|)
block|{
assert|assert
operator|!
name|parent
operator|.
name|openFiles
operator|.
name|contains
argument_list|(
name|name
argument_list|)
assert|;
name|parent
operator|.
name|openFiles
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeOpenFile
name|void
name|removeOpenFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|parent
operator|.
name|openFiles
init|)
block|{
assert|assert
name|parent
operator|.
name|openFiles
operator|.
name|contains
argument_list|(
name|name
argument_list|)
assert|;
name|parent
operator|.
name|openFiles
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|closedFiles
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|bytesUsed
name|void
name|bytesUsed
parameter_list|(
name|long
name|numBytes
parameter_list|)
block|{
name|ramAllocator
operator|.
name|bytesUsed
argument_list|(
name|numBytes
argument_list|)
expr_stmt|;
block|}
DECL|method|message
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|writer
operator|.
name|message
argument_list|(
literal|"DW: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

