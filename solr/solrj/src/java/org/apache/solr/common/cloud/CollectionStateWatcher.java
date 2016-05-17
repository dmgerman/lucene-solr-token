begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Callback registered with {@link ZkStateReader#registerCollectionStateWatcher(String, CollectionStateWatcher)}  * and called whenever the collection state changes.  */
end_comment

begin_interface
DECL|interface|CollectionStateWatcher
specifier|public
interface|interface
name|CollectionStateWatcher
block|{
comment|/**    * Called when the collection we are registered against has a change of state    *    * Note that, due to the way Zookeeper watchers are implemented, a single call may be    * the result of several state changes    *    * @param liveNodes       the set of live nodes    * @param collectionState the new collection state (may be null if the collection has been    *                        deleted)    *    * @return true if the watcher should be removed    */
DECL|method|onStateChanged
name|boolean
name|onStateChanged
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|DocCollection
name|collectionState
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

