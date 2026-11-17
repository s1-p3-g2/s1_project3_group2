const API_URL = 'http://localhost:8080';

export type RatingResponse = {
  userId: number;
  duckId: number;
  isPositive: boolean;
};

export async function getRatings(): Promise<RatingResponse[]> {
  const response = await fetch(`${API_URL}/api/v1/ratings`);
  const text = await response.text();

  console.log('GET status:', response.status);
  console.log('GET response:', text);

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  const data = text ? JSON.parse(text) : [];

  // Spring returns a Page object, so the actual list is usually in content.
  return Array.isArray(data)
      ? data
      : data.content ?? [];
}

export async function postRating(
    userId: number,
    duckId: number,
    ratingChoice: boolean
) {
  try {
    const request = {
      userId,
      duckId,
      isPositive: ratingChoice
    }
    const response = await fetch(`${API_URL}/api/v1/ratings`, {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    console.log('Fetch completed:', response.status);

    const responseText = await response.text();
    console.log('POST response:', responseText);

    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`);
    }

    return responseText ? JSON.parse(responseText) : null;
  } catch (error) {
    console.error('POST fetch failed:', error);
    throw error;
  }
}

export async function putRating(
    userId: number,
    duckId: number,
    ratingChoice: boolean
) {
  try {
    const request = {
      userId,
      duckId,
      isPositive: ratingChoice
    }
    const response = await fetch(`${API_URL}/api/v1/ratings`, {
      method: 'PUT',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    console.log('Fetch completed:', response.status);

    const responseText = await response.text();
    console.log('POST response:', responseText);

    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`);
    }

    return responseText ? JSON.parse(responseText) : null;
  } catch (error) {
    console.error('POST fetch failed:', error);
    throw error;
  }
}

export async function getRatingByDuck(duckId: number) {
  const response = await fetch(`${API_URL}/api/v1/ratings/ducks/${duckId}`);
  const text = await response.text();

  console.log('GET status:', response.status);
  console.log('GET response:', text);

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  const data = text ? JSON.parse(text) : [];

  // Spring returns a Page object, so the actual list is usually in content.
  return Array.isArray(data)
      ? data
      : data.content ?? [];
}

export async function getRatingByUser(userId: number) {
  const response = await fetch(`${API_URL}/api/v1/ratings/ducks/${userId}`);
  const text = await response.text();

  console.log('GET status:', response.status);
  console.log('GET response:', text);

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  const data = text ? JSON.parse(text) : [];

  // Spring returns a Page object, so the actual list is usually in content.
  return Array.isArray(data)
      ? data
      : data.content ?? [];
}

export async function getRating(duckId: number, userId: number) {
  const response = await fetch(`${API_URL}/api/v1/ratings/ducks/${duckId}/users/${userId}`);
  const text = await response.text();

  console.log('GET status:', response.status);
  console.log('GET response:', text);

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  const data = text ? JSON.parse(text) : [];

  // Spring returns a Page object, so the actual list is usually in content.
  return Array.isArray(data)
      ? data
      : data.content ?? [];
}