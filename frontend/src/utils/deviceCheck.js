/**
 * Helper to check if candidate is using a mobile phone or tablet
 */
export const isMobileDevice = () => {
  // check mobile browser user agent string
  const userAgent = navigator.userAgent || navigator.vendor || window.opera;
  const mobileRegex = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini|Mobile|Tablet/i;
  
  if (mobileRegex.test(userAgent)) {
    return true;
  }

  // check touch points for ipads and touch tablets
  if (navigator.maxTouchPoints && navigator.maxTouchPoints > 2 && window.innerWidth < 1024) {
    return true;
  }

  // fallback check for small mobile emulation screen width
  if (window.innerWidth <= 768) {
    return true;
  }

  return false;
};