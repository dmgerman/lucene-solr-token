begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Title:        LARM Lanlab Retrieval Machine<p>  * Description:<p>  * Copyright:    Copyright (c)<p>  * Company:<p>  * @author  * @version 1.0  */
end_comment

begin_package
DECL|package|de.lanlab.larm.fetcher
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|fetcher
package|;
end_package

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|threads
operator|.
name|ServerThread
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
operator|.
name|State
import|;
end_import

begin_comment
comment|/**  * a server thread for the thread pool that records the number  * of bytes read and the number of tasks run  * mainly for statistical purposes and to keep most of the information a task needs  * static  */
end_comment

begin_class
DECL|class|FetcherThread
specifier|public
class|class
name|FetcherThread
extends|extends
name|ServerThread
block|{
DECL|field|totalBytesRead
name|long
name|totalBytesRead
init|=
literal|0
decl_stmt|;
DECL|field|totalTasksRun
name|long
name|totalTasksRun
init|=
literal|0
decl_stmt|;
DECL|field|hostManager
name|HostManager
name|hostManager
decl_stmt|;
DECL|field|documentBuffer
name|byte
index|[]
name|documentBuffer
init|=
operator|new
name|byte
index|[
name|Constants
operator|.
name|FETCHERTASK_READSIZE
index|]
decl_stmt|;
DECL|method|getHostManager
specifier|public
name|HostManager
name|getHostManager
parameter_list|()
block|{
return|return
name|hostManager
return|;
block|}
DECL|method|FetcherThread
specifier|public
name|FetcherThread
parameter_list|(
name|int
name|threadNumber
parameter_list|,
name|ThreadGroup
name|threadGroup
parameter_list|,
name|HostManager
name|hostManager
parameter_list|)
block|{
name|super
argument_list|(
name|threadNumber
argument_list|,
literal|"FetcherThread "
operator|+
name|threadNumber
argument_list|,
name|threadGroup
argument_list|)
expr_stmt|;
name|this
operator|.
name|hostManager
operator|=
name|hostManager
expr_stmt|;
block|}
DECL|field|STATE_IDLE
specifier|public
specifier|static
name|String
name|STATE_IDLE
init|=
literal|"Idle"
decl_stmt|;
DECL|field|idleState
name|State
name|idleState
init|=
operator|new
name|State
argument_list|(
name|STATE_IDLE
argument_list|)
decl_stmt|;
comment|// only set if task is finished
DECL|method|taskReady
specifier|protected
name|void
name|taskReady
parameter_list|()
block|{
name|totalBytesRead
operator|+=
operator|(
operator|(
name|FetcherTask
operator|)
name|task
operator|)
operator|.
name|getBytesRead
argument_list|()
expr_stmt|;
name|totalTasksRun
operator|++
expr_stmt|;
name|super
operator|.
name|taskReady
argument_list|()
expr_stmt|;
name|idleState
operator|.
name|setState
argument_list|(
name|STATE_IDLE
argument_list|)
expr_stmt|;
block|}
DECL|method|getTotalBytesRead
specifier|public
name|long
name|getTotalBytesRead
parameter_list|()
block|{
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
return|return
name|totalBytesRead
operator|+
operator|(
operator|(
name|FetcherTask
operator|)
name|task
operator|)
operator|.
name|getBytesRead
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|totalBytesRead
return|;
block|}
block|}
DECL|method|getTotalTasksRun
specifier|public
name|long
name|getTotalTasksRun
parameter_list|()
block|{
return|return
name|totalTasksRun
return|;
block|}
DECL|method|getDocumentBuffer
specifier|public
name|byte
index|[]
name|getDocumentBuffer
parameter_list|()
block|{
return|return
name|documentBuffer
return|;
block|}
DECL|method|getTaskState
specifier|public
name|State
name|getTaskState
parameter_list|()
block|{
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
comment|// task could be null here
return|return
operator|(
operator|(
name|FetcherTask
operator|)
name|task
operator|)
operator|.
name|getTaskState
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|idleState
operator|.
name|cloneState
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

