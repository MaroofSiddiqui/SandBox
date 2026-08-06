import { Navigate } from "react-router-dom";

function ProtectedRoute({
  children,
  allowedRoles,
}) {

  const token = localStorage.getItem("token");

  let user = null;

  /*
   * Safely read stored user.
   *
   * JSON.parse can throw an error if localStorage
   * contains invalid/corrupted JSON.
   */
  try {
    const storedUser =
      localStorage.getItem("user");

    user = storedUser
      ? JSON.parse(storedUser)
      : null;

  } catch {
    localStorage.removeItem("user");
    localStorage.removeItem("token");

    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }


  /*
   * NOT AUTHENTICATED
   *
   * User has no token or stored user.
   */
  if (!token || !user) {

    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }


  /*
   * AUTHENTICATED BUT NOT AUTHORIZED
   *
   * Example:
   *
   * CANDIDATE tries /super-admin
   * HR tries /super-admin
   * CANDIDATE tries /hr
   *
   * Send them to 403 page.
   */
  if (
    allowedRoles &&
    !allowedRoles.includes(user.role)
  ) {

    return (
      <Navigate
        to="/access-denied"
        replace
      />
    );
  }


  /*
   * AUTHENTICATED + AUTHORIZED
   */
  return children;
}

export default ProtectedRoute;