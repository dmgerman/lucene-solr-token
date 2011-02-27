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
name|util
operator|.
name|Iterator
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
name|locks
operator|.
name|ReentrantLock
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

begin_class
DECL|class|DocumentsWriterPerThreadPool
specifier|public
specifier|abstract
class|class
name|DocumentsWriterPerThreadPool
block|{
DECL|class|ThreadState
specifier|final
specifier|static
class|class
name|ThreadState
extends|extends
name|ReentrantLock
block|{
DECL|field|perThread
specifier|final
name|DocumentsWriterPerThread
name|perThread
decl_stmt|;
DECL|method|ThreadState
name|ThreadState
parameter_list|(
name|DocumentsWriterPerThread
name|perThread
parameter_list|)
block|{
name|this
operator|.
name|perThread
operator|=
name|perThread
expr_stmt|;
block|}
block|}
DECL|field|perThreads
specifier|private
specifier|final
name|ThreadState
index|[]
name|perThreads
decl_stmt|;
DECL|field|numThreadStatesActive
specifier|private
specifier|volatile
name|int
name|numThreadStatesActive
decl_stmt|;
DECL|method|DocumentsWriterPerThreadPool
specifier|public
name|DocumentsWriterPerThreadPool
parameter_list|(
name|int
name|maxNumPerThreads
parameter_list|)
block|{
name|maxNumPerThreads
operator|=
operator|(
name|maxNumPerThreads
operator|<
literal|1
operator|)
condition|?
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_THREAD_STATES
else|:
name|maxNumPerThreads
expr_stmt|;
name|this
operator|.
name|perThreads
operator|=
operator|new
name|ThreadState
index|[
name|maxNumPerThreads
index|]
expr_stmt|;
name|numThreadStatesActive
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|(
name|DocumentsWriter
name|documentsWriter
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|perThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|perThreads
index|[
name|i
index|]
operator|=
operator|new
name|ThreadState
argument_list|(
operator|new
name|DocumentsWriterPerThread
argument_list|(
name|documentsWriter
operator|.
name|directory
argument_list|,
name|documentsWriter
argument_list|,
name|fieldInfos
argument_list|,
name|documentsWriter
operator|.
name|chain
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMaxThreadStates
specifier|public
name|int
name|getMaxThreadStates
parameter_list|()
block|{
return|return
name|perThreads
operator|.
name|length
return|;
block|}
DECL|method|newThreadState
specifier|public
specifier|synchronized
name|ThreadState
name|newThreadState
parameter_list|()
block|{
if|if
condition|(
name|numThreadStatesActive
operator|<
name|perThreads
operator|.
name|length
condition|)
block|{
name|ThreadState
name|state
init|=
name|perThreads
index|[
name|numThreadStatesActive
index|]
decl_stmt|;
name|numThreadStatesActive
operator|++
expr_stmt|;
return|return
name|state
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getAndLock
specifier|public
specifier|abstract
name|ThreadState
name|getAndLock
parameter_list|(
name|Thread
name|requestingThread
parameter_list|,
name|DocumentsWriter
name|documentsWriter
parameter_list|,
name|Document
name|doc
parameter_list|)
function_decl|;
DECL|method|clearThreadBindings
specifier|public
specifier|abstract
name|void
name|clearThreadBindings
parameter_list|(
name|ThreadState
name|perThread
parameter_list|)
function_decl|;
DECL|method|clearAllThreadBindings
specifier|public
specifier|abstract
name|void
name|clearAllThreadBindings
parameter_list|()
function_decl|;
DECL|method|getAllPerThreadsIterator
specifier|public
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|getAllPerThreadsIterator
parameter_list|()
block|{
return|return
name|getPerThreadsIterator
argument_list|(
name|this
operator|.
name|perThreads
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|getActivePerThreadsIterator
specifier|public
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|getActivePerThreadsIterator
parameter_list|()
block|{
return|return
name|getPerThreadsIterator
argument_list|(
name|this
operator|.
name|numThreadStatesActive
argument_list|)
return|;
block|}
DECL|method|getPerThreadsIterator
specifier|private
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|getPerThreadsIterator
parameter_list|(
specifier|final
name|int
name|upto
parameter_list|)
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
argument_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|i
operator|<
name|upto
return|;
block|}
specifier|public
name|ThreadState
name|next
parameter_list|()
block|{
return|return
name|perThreads
index|[
name|i
operator|++
index|]
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove() not supported."
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

