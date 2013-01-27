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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/** Tracks live field values across NRT reader reopens.  *  This holds a map for all updated ids since  *  the last reader reopen.  Once the NRT reader is reopened,  *  it prunes the map.  This means you must reopen your NRT  *  reader periodically otherwise the RAM consumption of  *  this class will grow unbounded!  *  *<p>NOTE: you must ensure the same id is never updated at  *  the same time by two threads, because in this case you  *  cannot in general know which thread "won". */
end_comment

begin_class
DECL|class|LiveFieldValues
specifier|public
specifier|abstract
class|class
name|LiveFieldValues
parameter_list|<
name|T
parameter_list|>
implements|implements
name|ReferenceManager
operator|.
name|RefreshListener
implements|,
name|Closeable
block|{
DECL|field|current
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|current
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|old
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|old
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|mgr
specifier|private
specifier|final
name|ReferenceManager
argument_list|<
name|IndexSearcher
argument_list|>
name|mgr
decl_stmt|;
DECL|field|missingValue
specifier|private
specifier|final
name|T
name|missingValue
decl_stmt|;
DECL|method|LiveFieldValues
specifier|public
name|LiveFieldValues
parameter_list|(
name|ReferenceManager
argument_list|<
name|IndexSearcher
argument_list|>
name|mgr
parameter_list|,
name|T
name|missingValue
parameter_list|)
block|{
name|this
operator|.
name|missingValue
operator|=
name|missingValue
expr_stmt|;
name|this
operator|.
name|mgr
operator|=
name|mgr
expr_stmt|;
name|mgr
operator|.
name|addListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|mgr
operator|.
name|removeListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeRefresh
specifier|public
name|void
name|beforeRefresh
parameter_list|()
throws|throws
name|IOException
block|{
name|old
operator|=
name|current
expr_stmt|;
comment|// Start sending all updates after this point to the new
comment|// map.  While reopen is running, any lookup will first
comment|// try this new map, then fallback to old, then to the
comment|// current searcher:
name|current
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|afterRefresh
specifier|public
name|void
name|afterRefresh
parameter_list|(
name|boolean
name|didRefresh
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Now drop all the old values because they are now
comment|// visible via the searcher that was just opened; if
comment|// didRefresh is false, it's possible old has some
comment|// entries in it, which is fine: it means they were
comment|// actually already included in the previously opened
comment|// reader.  So we can safely clear old here:
name|old
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/** Call this after you've successfully added a document    *  to the index, to record what value you just set the    *  field to. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|id
parameter_list|,
name|T
name|value
parameter_list|)
block|{
name|current
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Call this after you've successfully deleted a document    *  from the index. */
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|current
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|missingValue
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the [approximate] number of id/value pairs    *  buffered in RAM. */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|current
operator|.
name|size
argument_list|()
operator|+
name|old
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** Returns the current value for this id, or null if the    *  id isn't in the index or was deleted. */
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
comment|// First try to get the "live" value:
name|T
name|value
init|=
name|current
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
name|missingValue
condition|)
block|{
comment|// Deleted but the deletion is not yet reflected in
comment|// the reader:
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
else|else
block|{
name|value
operator|=
name|old
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|==
name|missingValue
condition|)
block|{
comment|// Deleted but the deletion is not yet reflected in
comment|// the reader:
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
else|else
block|{
comment|// It either does not exist in the index, or, it was
comment|// already flushed& NRT reader was opened on the
comment|// segment, so fallback to current searcher:
name|IndexSearcher
name|s
init|=
name|mgr
operator|.
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|lookupFromSearcher
argument_list|(
name|s
argument_list|,
name|id
argument_list|)
return|;
block|}
finally|finally
block|{
name|mgr
operator|.
name|release
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** This is called when the id/value was already flushed& opened    *  in an NRT IndexSearcher.  You must implement this to    *  go look up the value (eg, via doc values, field cache,    *  stored fields, etc.). */
DECL|method|lookupFromSearcher
specifier|protected
specifier|abstract
name|T
name|lookupFromSearcher
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

