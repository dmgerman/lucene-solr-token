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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|Reference
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
name|ReferenceQueue
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
comment|/**  * Implements a combination of {@link java.util.WeakHashMap} and  * {@link java.util.IdentityHashMap}.  * Useful for caches that need to key off of a {@code ==} comparison  * instead of a {@code .equals}.  *   *<p>This class is not a general-purpose {@link java.util.Map}  * implementation! It intentionally violates  * Map's general contract, which mandates the use of the equals method  * when comparing objects. This class is designed for use only in the  * rare cases wherein reference-equality semantics are required.  *   *<p>This implementation was forked from<a href="http://cxf.apache.org/">Apache CXF</a>  * but modified to<b>not</b> implement the {@link java.util.Map} interface and  * without any set/iterator views on it, as those are error-prone  * and inefficient, if not implemented carefully. Lucene's implementation also  * supports {@code null} keys, but those are never weak!  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|WeakIdentityMap
specifier|public
specifier|final
class|class
name|WeakIdentityMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
DECL|field|queue
specifier|private
specifier|final
name|ReferenceQueue
argument_list|<
name|Object
argument_list|>
name|queue
init|=
operator|new
name|ReferenceQueue
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|backingStore
specifier|private
specifier|final
name|Map
argument_list|<
name|IdentityWeakReference
argument_list|,
name|V
argument_list|>
name|backingStore
decl_stmt|;
comment|/** Creates a new {@code WeakIdentityMap} based on a non-synchronized {@link HashMap}. */
DECL|method|newHashMap
specifier|public
specifier|static
specifier|final
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|WeakIdentityMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newHashMap
parameter_list|()
block|{
return|return
operator|new
name|WeakIdentityMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
operator|new
name|HashMap
argument_list|<
name|IdentityWeakReference
argument_list|,
name|V
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
comment|/** Creates a new {@code WeakIdentityMap} based on a {@link ConcurrentHashMap}. */
DECL|method|newConcurrentHashMap
specifier|public
specifier|static
specifier|final
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|WeakIdentityMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newConcurrentHashMap
parameter_list|()
block|{
return|return
operator|new
name|WeakIdentityMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|IdentityWeakReference
argument_list|,
name|V
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
DECL|method|WeakIdentityMap
specifier|private
name|WeakIdentityMap
parameter_list|(
name|Map
argument_list|<
name|IdentityWeakReference
argument_list|,
name|V
argument_list|>
name|backingStore
parameter_list|)
block|{
name|this
operator|.
name|backingStore
operator|=
name|backingStore
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|backingStore
operator|.
name|clear
argument_list|()
expr_stmt|;
name|reap
argument_list|()
expr_stmt|;
block|}
DECL|method|containsKey
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|containsKey
argument_list|(
operator|new
name|IdentityWeakReference
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|get
argument_list|(
operator|new
name|IdentityWeakReference
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
DECL|method|put
specifier|public
name|V
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|put
argument_list|(
operator|new
name|IdentityWeakReference
argument_list|(
name|key
argument_list|,
name|queue
argument_list|)
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|remove
specifier|public
name|V
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|remove
argument_list|(
operator|new
name|IdentityWeakReference
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
if|if
condition|(
name|backingStore
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|0
return|;
name|reap
argument_list|()
expr_stmt|;
return|return
name|backingStore
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|reap
specifier|private
name|void
name|reap
parameter_list|()
block|{
name|Reference
argument_list|<
name|?
argument_list|>
name|zombie
decl_stmt|;
while|while
condition|(
operator|(
name|zombie
operator|=
name|queue
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|backingStore
operator|.
name|remove
argument_list|(
name|zombie
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|IdentityWeakReference
specifier|private
specifier|static
specifier|final
class|class
name|IdentityWeakReference
extends|extends
name|WeakReference
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|hash
specifier|private
specifier|final
name|int
name|hash
decl_stmt|;
DECL|method|IdentityWeakReference
name|IdentityWeakReference
parameter_list|(
name|Object
name|obj
parameter_list|,
name|ReferenceQueue
argument_list|<
name|Object
argument_list|>
name|queue
parameter_list|)
block|{
name|super
argument_list|(
name|obj
operator|==
literal|null
condition|?
name|NULL
else|:
name|obj
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|hash
operator|=
name|System
operator|.
name|identityHashCode
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hash
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|IdentityWeakReference
condition|)
block|{
specifier|final
name|IdentityWeakReference
name|ref
init|=
operator|(
name|IdentityWeakReference
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|get
argument_list|()
operator|==
name|ref
operator|.
name|get
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|// we keep a hard reference to our NULL key, so map supports null keys that never get GCed:
DECL|field|NULL
specifier|private
specifier|static
specifier|final
name|Object
name|NULL
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
block|}
block|}
end_class

end_unit

