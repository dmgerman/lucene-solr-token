begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.storage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
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
name|gdata
operator|.
name|data
operator|.
name|GDataAccount
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseFeed
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseFeed
import|;
end_import

begin_comment
comment|/**  * A interface every storage implementation must provide to access the  *<tt>Storage</tt>. It describes all access methodes needed to store,  * retrieve and look up data stored in the<tt>Storage</tt> component. This  * interface acts as a<tt>Facade</tt> to hide the storage implementation from  * the user.  *<p>  * This could also act as a proxy for a remote storage. It also removes any  * restrictions from custom storage implementations.  *</p>  *   *   * @author Simon Willnauer  *   */
end_comment

begin_comment
comment|/*  * not final yet  */
end_comment

begin_interface
DECL|interface|Storage
specifier|public
interface|interface
name|Storage
block|{
comment|/**      *       * Stores the given entry. The ServerBaseEntry must provide a feed id and      * the service type. configuration for the entry.      *       * @param entry -      *            the entry to store      *       * @return - the stored Entry for the server response      * @throws StorageException -      *             if the entry can not be stored or required field are not set.      */
DECL|method|storeEntry
specifier|public
specifier|abstract
name|BaseEntry
name|storeEntry
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Deletes the given entry. The ServerBaseEntry just hase to provide the      * entry id to be deleted.      *       * @param entry -      *            the entry to delete from the storage      * @throws StorageException -      *             if the entry can not be deleted or the entry does not exist      *             or required field are not set.      */
DECL|method|deleteEntry
specifier|public
specifier|abstract
name|void
name|deleteEntry
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Updates the given entry. The ServerBaseEntry must provide a feed id,      * service id and the      * {@link org.apache.lucene.gdata.server.registry.ProvidedService}      *       * @param entry -      *            the entry to update      *       * @return - the updated entry for server response.      * @throws StorageException -      *             if the entry can not be updated or does not exist or required      *             field are not set.      */
DECL|method|updateEntry
specifier|public
specifier|abstract
name|BaseEntry
name|updateEntry
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Retrieves the requested feed from the storage. The given ServerBaseFeed      * must provide information about the feed id, max-result count and the      * start index. To create feeds and entries also the service type must be      * provided.      *       * @param feed -      *            the to retieve from the storage      * @return the requested feed      * @throws StorageException -      *             the feed does not exist or can not be retrieved or required      *             field are not set.      */
DECL|method|getFeed
specifier|public
specifier|abstract
name|BaseFeed
name|getFeed
parameter_list|(
name|ServerBaseFeed
name|feed
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Retrieves the requested entry from the storage. The given entry must      * provide information about the entry id and service type.      *       * @param entry -      *            the entry to retrieve      * @return - the requested entry      * @throws StorageException -      *             if the entry does not exist or can not be created or required      *             field are not set.      */
DECL|method|getEntry
specifier|public
specifier|abstract
name|BaseEntry
name|getEntry
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Saves a new account. Required attributes to set are<tt>password</tt>      * and<tt>accountname</tt>      *       * @param account -      *            the account to save      * @throws StorageException -      *             if the account can not be stored or the account already      *             exists or required field are not set.      */
DECL|method|storeAccount
specifier|public
specifier|abstract
name|void
name|storeAccount
parameter_list|(
specifier|final
name|GDataAccount
name|account
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Updates an existing account. Required attributes to set are      *<tt>password</tt> and<tt>accountname</tt>      *       * @param account -      *            the account to update      * @throws StorageException -      *             if the account does not exist or required field are not set.      */
DECL|method|updateAccount
specifier|public
specifier|abstract
name|void
name|updateAccount
parameter_list|(
specifier|final
name|GDataAccount
name|account
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Deletes the account for the given account name. All feeds and entries      * referencing this account will be deleted as well!      *       * @param accountname -      *            the name of the account to delete      * @throws StorageException -      *             if the account does not exist      */
DECL|method|deleteAccount
specifier|public
specifier|abstract
name|void
name|deleteAccount
parameter_list|(
specifier|final
name|String
name|accountname
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Stores a new feed for a existing account. The Feed must provide      * information about the service type to store the feed for and the feed id      * used for accessing and retrieving the feed from the storage. Each feed is      * associated with a provided service. This method does check wheather a      * feed with the same feed id as the given feed does already exists.      *       * @see org.apache.lucene.gdata.server.registry.ProvidedService      * @param feed -      *            the feed to create      * @param accountname -      *            the account name belongs to the feed      * @throws StorageException -      *             if the feed already exists or the feed can not be stored      */
DECL|method|storeFeed
specifier|public
specifier|abstract
name|void
name|storeFeed
parameter_list|(
specifier|final
name|ServerBaseFeed
name|feed
parameter_list|,
name|String
name|accountname
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Deletes the feed for the given feed id. All Entries referencing the given      * feed id will be deleted as well.      *       * @param feedId -      *            the feed id for the feed to delete.      * @throws StorageException -      *             if the feed for the feed id does not exist or the feed can      *             not be deleted      */
DECL|method|deleteFeed
specifier|public
specifier|abstract
name|void
name|deleteFeed
parameter_list|(
specifier|final
name|String
name|feedId
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Updates a stored feed. The Feed must provide information about the      * service type to store the feed for and the feed id used for accessing and      * retrieving the feed from the storage.      *       * @param feed -      *            the feed to update      * @param accountname -      *            the account name belongs to the feed      * @throws StorageException -      *             if the feed does not exist or the feed can not be updated      */
DECL|method|updateFeed
specifier|public
specifier|abstract
name|void
name|updateFeed
parameter_list|(
specifier|final
name|ServerBaseFeed
name|feed
parameter_list|,
name|String
name|accountname
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Retrieves the service name for a stored feed      *       * @param feedId -      *            the feed id      * @return - the name of the service      * @throws StorageException -      *             if no feed for the provided id is stored      */
DECL|method|getServiceForFeed
specifier|public
specifier|abstract
name|String
name|getServiceForFeed
parameter_list|(
name|String
name|feedId
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * @param accountName -      *            the name of the requested account      * @return - a {@link GDataAccount} instance for the requested account name      * @throws StorageException -      *             if no account for the account name is stored      *       */
DECL|method|getAccount
specifier|public
specifier|abstract
name|GDataAccount
name|getAccount
parameter_list|(
name|String
name|accountName
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * close this storage instance. This method will be called by clients after      * use.      */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
comment|/**      * Each feed belongs to one specific account. This method retrieves the      * account name for      *       * @param feedId -      *            the id of the feed to retrieve the accountname      * @return - the name / id of the account associated with the feed for the      *         given feed id      * @throws StorageException -      *             if the feed is not stored or the storage can not be accessed      */
DECL|method|getAccountNameForFeedId
specifier|public
name|String
name|getAccountNameForFeedId
parameter_list|(
name|String
name|feedId
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Retrieves the date of the last modification for the given id      *       * @param entryId -      *            the entry Id      * @param feedId -      *            the feed which contains the entry      * @return - The date of the last modifiaction in milliseconds or      *<code>new Long(0)</code> if the resource can not be found eg.      *         the time can not be accessed      * @throws StorageException -      *             if the storage can not be accessed      */
DECL|method|getEntryLastModified
specifier|public
name|Long
name|getEntryLastModified
parameter_list|(
name|String
name|entryId
parameter_list|,
name|String
name|feedId
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Retrieves the date of the last modification for the given id      *       * @param feedId -      *            the feed Id      * @return - The date of the last modifiaction in milliseconds or      *<code>new Long(0)</code> if the resource can not be found eg.      *         the time can not be accessed      * @throws StorageException -      *             if the storage can not be accessed      */
DECL|method|getFeedLastModified
specifier|public
name|Long
name|getFeedLastModified
parameter_list|(
name|String
name|feedId
parameter_list|)
throws|throws
name|StorageException
function_decl|;
block|}
end_interface

end_unit

