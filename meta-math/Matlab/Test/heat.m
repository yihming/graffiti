function q = calculate_heat_flux(k, T_front, T_back, x_front, x_back)
  % Here is a comment.
  q = k * (T_back - T_front) / (x_back - x_front);
  return
