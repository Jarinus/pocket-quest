package nl.pocketquest.server.request.handler

import nl.pocketquest.server.request.Request

abstract class RequestHandler<in T : Request> {

    abstract fun listen()

    protected fun handle(request: T): Boolean {
        return validateRequest(request) && processRequest(request)
    }

    /**
     * Validates the request's contents. Note: the request's type is already validated at this point.
     */
    abstract protected fun validateRequest(request: T): Boolean

    /**
     * Processes the request. This is called when all validations pass.
     */
    abstract protected fun processRequest(request: T): Boolean
}
