begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|HashMap
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
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_comment
comment|/** Java's builtin ThreadLocal has a serious flaw:  *  it can take an arbitrarily long amount of time to  *  dereference the things you had stored in it, even once the  *  ThreadLocal instance itself is no longer referenced.  *  This is because there is single, master map stored for  *  each thread, which all ThreadLocals share, and that  *  master map only periodically purges "stale" entries.  *  *  While not technically a memory leak, because eventually  *  the memory will be reclaimed, it can take a long time  *  and you can easily hit OutOfMemoryError because from the  *  GC's standpoint the stale entries are not reclaimable.  *   *  This class works around that, by only enrolling  *  WeakReference values into the ThreadLocal, and  *  separately holding a hard reference to each stored  *  value.  When you call {@link #close}, these hard  *  references are cleared and then GC is freely able to  *  reclaim space by objects stored in it. */
end_comment

begin_class
DECL|class|CloseableThreadLocal
specifier|public
class|class
name|CloseableThreadLocal
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|t
specifier|private
name|ThreadLocal
argument_list|<
name|WeakReference
argument_list|<
name|T
argument_list|>
argument_list|>
name|t
init|=
operator|new
name|ThreadLocal
argument_list|<
name|WeakReference
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|hardRefs
specifier|private
name|Map
argument_list|<
name|Thread
argument_list|,
name|T
argument_list|>
name|hardRefs
init|=
operator|new
name|HashMap
argument_list|<
name|Thread
argument_list|,
name|T
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|initialValue
specifier|protected
name|T
name|initialValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|get
specifier|public
name|T
name|get
parameter_list|()
block|{
name|WeakReference
argument_list|<
name|T
argument_list|>
name|weakRef
init|=
name|t
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|weakRef
operator|==
literal|null
condition|)
block|{
name|T
name|iv
init|=
name|initialValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|iv
operator|!=
literal|null
condition|)
block|{
name|set
argument_list|(
name|iv
argument_list|)
expr_stmt|;
return|return
name|iv
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|weakRef
operator|.
name|get
argument_list|()
return|;
block|}
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|T
name|object
parameter_list|)
block|{
name|t
operator|.
name|set
argument_list|(
operator|new
name|WeakReference
argument_list|<
name|T
argument_list|>
argument_list|(
name|object
argument_list|)
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|hardRefs
init|)
block|{
name|hardRefs
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|object
argument_list|)
expr_stmt|;
comment|// Purge dead threads
for|for
control|(
name|Iterator
argument_list|<
name|Thread
argument_list|>
name|it
init|=
name|hardRefs
operator|.
name|keySet
argument_list|()
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
specifier|final
name|Thread
name|t
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|t
operator|.
name|isAlive
argument_list|()
condition|)
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// Clear the hard refs; then, the only remaining refs to
comment|// all values we were storing are weak (unless somewhere
comment|// else is still using them) and so GC may reclaim them:
name|hardRefs
operator|=
literal|null
expr_stmt|;
name|t
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

