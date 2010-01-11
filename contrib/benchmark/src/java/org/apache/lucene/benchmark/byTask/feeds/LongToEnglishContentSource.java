begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package

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
name|English
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
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|LongToEnglishContentSource
specifier|public
class|class
name|LongToEnglishContentSource
extends|extends
name|ContentSource
block|{
DECL|field|counter
specifier|private
name|long
name|counter
init|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|10
decl_stmt|;
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{    }
comment|//TODO: reduce/clean up synchonization
DECL|method|getNextDocData
specifier|public
specifier|synchronized
name|DocData
name|getNextDocData
parameter_list|(
name|DocData
name|docData
parameter_list|)
throws|throws
name|NoMoreDataException
throws|,
name|IOException
block|{
name|docData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docData
operator|.
name|setBody
argument_list|(
name|English
operator|.
name|longToEnglish
argument_list|(
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setName
argument_list|(
literal|"doc_"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setTitle
argument_list|(
literal|"title_"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setDate
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|counter
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|counter
operator|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|10
expr_stmt|;
comment|//loop around
block|}
name|counter
operator|++
expr_stmt|;
return|return
name|docData
return|;
block|}
annotation|@
name|Override
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|counter
operator|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|10
expr_stmt|;
block|}
block|}
end_class

end_unit

