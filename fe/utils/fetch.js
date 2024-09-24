import toast from "react-hot-toast";

const apiUrl = "http://localhost:8080/api";

export default function _fetch(endpoint, method, body, customHeaders = {}) {
  const token = localStorage.getItem("jwtToken");

  return fetch(apiUrl + endpoint, {
    method,
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
      ...customHeaders, // Ekstra header'lar burada eklenir
    },
    body: body ? JSON.stringify(body) : undefined, // Body varsa JSON string'e çevir
  })
    .then((response) => {
      if (response.status === 401) {
        // Unauthorized ise kullanıcıyı login sayfasına yönlendir
        localStorage.removeItem("jwtToken");
        window.location.href = "/login";
        return;
      }
      if (!response.ok) {
        // 200 dışında bir yanıt kodu varsa hata fırlat
        throw new Error(`Error: ${response.statusText}`);
      }
      if (response.status === 204) return null; // No content (204) varsa null döner
      return response.json(); // JSON yanıtı döner
    })
    .catch((error) => {
      // Genel bir hata durumunda toast ile mesaj gösterir
      toast.error(error.message || "An unexpected error occurred.");
      throw error; // Hata üst seviye fonksiyonlara iletilir
    });
}
