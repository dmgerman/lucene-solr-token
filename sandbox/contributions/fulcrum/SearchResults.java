begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Category
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
name|search
operator|.
name|Hits
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
name|ArrayList
import|;
end_import

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
name|List
import|;
end_import

begin_import
import|import
name|search
operator|.
name|SearchResultFactory
import|;
end_import

begin_comment
comment|/**  *<p>  * Encapsulates the results of a search. After a SearchResults has  * been constructed from a Hits object, the IndexSearcher can be  * safely closed.  *</p>  *<p>  * SearchResults also provides a way of retrieving Java objects from  * Documents (via {@link search.SearchResultsFactory}).  *</p>  *<p>  *<b>Note that this implementation uses code from  * /projects/appex/search.</b>  *</p>  */
end_comment

begin_class
DECL|class|SearchResults
specifier|public
class|class
name|SearchResults
block|{
DECL|field|cat
specifier|private
specifier|static
name|Category
name|cat
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|SearchResults
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|hitsDocuments
specifier|private
name|List
name|hitsDocuments
decl_stmt|;
DECL|field|objectResults
specifier|private
name|List
name|objectResults
decl_stmt|;
DECL|field|totalNumberOfResults
specifier|private
name|int
name|totalNumberOfResults
decl_stmt|;
DECL|method|SearchResults
specifier|public
name|SearchResults
parameter_list|(
name|Hits
name|hits
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|hits
argument_list|,
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|SearchResults
specifier|public
name|SearchResults
parameter_list|(
name|Hits
name|hits
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
throws|throws
name|IOException
block|{
name|hitsDocuments
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|totalNumberOfResults
operator|=
name|hits
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|to
operator|>
name|totalNumberOfResults
condition|)
block|{
name|to
operator|=
name|totalNumberOfResults
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|from
init|;
name|i
operator|<
name|to
condition|;
name|i
operator|++
control|)
block|{
name|hitsDocuments
operator|.
name|add
argument_list|(
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getTotalNumberOfResults
specifier|public
name|int
name|getTotalNumberOfResults
parameter_list|()
block|{
return|return
name|totalNumberOfResults
return|;
block|}
comment|/**      * Obtain the results of the search as objects.      */
DECL|method|getResultsAsObjects
specifier|public
name|List
name|getResultsAsObjects
parameter_list|()
block|{
if|if
condition|(
name|objectResults
operator|==
literal|null
condition|)
block|{
name|objectResults
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|hitsDocuments
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
try|try
block|{
name|Object
name|o
init|=
name|SearchResultFactory
operator|.
name|getDocAsObject
argument_list|(
operator|(
name|Document
operator|)
name|it
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|objectResults
operator|.
name|contains
argument_list|(
name|o
argument_list|)
condition|)
name|objectResults
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
literal|"Error instantiating an object from a document."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|objectResults
return|;
block|}
block|}
end_class

end_unit

