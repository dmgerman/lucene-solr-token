begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling.suggest.jaspell
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
operator|.
name|jaspell
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
operator|.
name|Lookup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
operator|.
name|UnsortedTermFreqIteratorWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|SortedIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|TermFreqIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|JaspellLookup
specifier|public
class|class
name|JaspellLookup
extends|extends
name|Lookup
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JaspellLookup
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|trie
name|JaspellTernarySearchTrie
name|trie
decl_stmt|;
DECL|field|usePrefix
specifier|private
name|boolean
name|usePrefix
init|=
literal|true
decl_stmt|;
DECL|field|editDistance
specifier|private
name|int
name|editDistance
init|=
literal|2
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|config
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"init: "
operator|+
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|TermFreqIterator
name|tfit
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tfit
operator|instanceof
name|SortedIterator
condition|)
block|{
comment|// make sure it's unsorted
name|tfit
operator|=
operator|new
name|UnsortedTermFreqIteratorWrapper
argument_list|(
name|tfit
argument_list|)
expr_stmt|;
block|}
name|trie
operator|=
operator|new
name|JaspellTernarySearchTrie
argument_list|()
expr_stmt|;
name|trie
operator|.
name|setMatchAlmostDiff
argument_list|(
name|editDistance
argument_list|)
expr_stmt|;
while|while
condition|(
name|tfit
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|tfit
operator|.
name|next
argument_list|()
decl_stmt|;
name|float
name|freq
init|=
name|tfit
operator|.
name|freq
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|trie
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|Float
argument_list|(
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|trie
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// XXX
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|trie
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|List
argument_list|<
name|LookupResult
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
decl_stmt|;
name|int
name|count
init|=
name|onlyMorePopular
condition|?
name|num
operator|*
literal|2
else|:
name|num
decl_stmt|;
if|if
condition|(
name|usePrefix
condition|)
block|{
name|list
operator|=
name|trie
operator|.
name|matchPrefix
argument_list|(
name|key
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|list
operator|=
name|trie
operator|.
name|matchAlmost
argument_list|(
name|key
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|list
operator|==
literal|null
operator|||
name|list
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|res
return|;
block|}
name|int
name|maxCnt
init|=
name|Math
operator|.
name|min
argument_list|(
name|num
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|onlyMorePopular
condition|)
block|{
name|LookupPriorityQueue
name|queue
init|=
operator|new
name|LookupPriorityQueue
argument_list|(
name|num
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|list
control|)
block|{
name|float
name|freq
init|=
operator|(
name|Float
operator|)
name|trie
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|queue
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|s
argument_list|,
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|LookupResult
name|lr
range|:
name|queue
operator|.
name|getResults
argument_list|()
control|)
block|{
name|res
operator|.
name|add
argument_list|(
name|lr
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
name|maxCnt
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|float
name|freq
init|=
operator|(
name|Float
operator|)
name|trie
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|s
argument_list|,
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|boolean
name|load
parameter_list|(
name|File
name|storeDir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|store
specifier|public
name|boolean
name|store
parameter_list|(
name|File
name|storeDir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

